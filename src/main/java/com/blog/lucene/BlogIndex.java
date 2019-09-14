package com.blog.lucene;

import com.blog.entity.Blog;
import com.blog.util.DateUtil;
import com.blog.util.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 使用Lucene对博客进行增删改查
 */
public class BlogIndex {

    //指定文件夹，索引库。为D：\\Lucene
    private Directory dir = null;
    private String lucenePath = "D:\\Lucene";

    /**
     * 获取Lucene的写入方法
     */
    private IndexWriter getIndexWriter()throws Exception{
        dir = FSDirectory.open(Paths.get(lucenePath));
        SmartChineseAnalyzer smartChineseAnalyzer = new SmartChineseAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(smartChineseAnalyzer);
        IndexWriter indexWriter = new IndexWriter(dir,indexWriterConfig);
        return indexWriter;
    }

    /**
     * 增加索引【该方法是对一个文档（博客）进行的操作，如果多个要进行遍历调用】
     */
    public void addIndex(Blog blog)throws Exception{
        IndexWriter indexWriter = getIndexWriter();
        Document document = new Document();
        document.add(new StringField("id",String.valueOf(blog.getId()), Field.Store.YES));
        document.add(new TextField("title",blog.getTitle(), Field.Store.YES));
        document.add(new StringField("releaseDate", DateUtil.formatDate("yyyy-MM-dd"), Field.Store.YES));
        document.add(new TextField("content",blog.getContentNoTag(), Field.Store.YES));
        document.add(new TextField("keyWord",blog.getKeyWord(), Field.Store.YES));
        indexWriter.addDocument(document);
        indexWriter.close();
    }

    /**
     * 更新索引
     * Term 是搜索的基本单位，一个 Term 对象有两个 String 类型的域组成。
     * 生成一个 Term 对象可以有如下一条语句来完成：
     * Term term = new Term(“fieldName”,”queryWord”);
     * 其中第一个参数代表了要在文档的哪一个 Field 上进行查找，
     * 第二个参数代表了要查询的关键词。
     */
    public void updateIndex(Blog blog)throws Exception{
        deleteIndex(blog.getId().toString());
        IndexWriter indexWriter = getIndexWriter();
        Document document = new Document();
        document.add(new StringField("id",String.valueOf(blog.getId()), Field.Store.YES));
        document.add(new TextField("title",blog.getTitle(), Field.Store.YES));
        document.add(new StringField("releaseDate", DateUtil.formatDate("yyyy-MM-dd"), Field.Store.YES));
        document.add(new TextField("content",blog.getContentNoTag(), Field.Store.YES));
        document.add(new TextField("keyWord",blog.getKeyWord(), Field.Store.YES));
        //代表根据id这个域进行更新，也就是id不可以更新。【id为主键】
        indexWriter.updateDocument(new Term("id"),document);
        indexWriter.close();
    }

    /**
     * 删除主键
     */
    public void deleteIndex(String blogId)throws Exception{
        IndexWriter indexWriter = getIndexWriter();
        indexWriter.deleteDocuments(new Term[]{new Term("id",blogId)});
        indexWriter.forceMergeDeletes();
        indexWriter.commit();
        indexWriter.close();
    }

    /**
     * 搜索索引：根据用户传来的string值，将string值以及创建索引的，分析器/域,传入到查询条件中。
     * 通过查询条件到索引库中查询，返回文档对象编号，通过编号获取到相应的文档对象。
     * 根据文档对象最终获取到对应的实体（博客【符合查询条件的博客集合】）
     */
    public List<Blog> searchBlog(String q)throws Exception{
        List<Blog> blogList = new LinkedList<>();
        dir = FSDirectory.open(Paths.get(lucenePath));
        //获取reader
        IndexReader indexReader = DirectoryReader.open(dir);
        //获取流
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        //创建分析器，供给用户输入的字符串进行分析，拆分为与索引库中的索引值类似，
        // 然后才可以根据用户输入的内容作为索引去索引库中找对应的一系列文档等。
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();

        //创建查询条件
        //将用户输出的内容看做是输入的是title这个域处理，然后将用户输入的string（q），
        // 进行analyzer分析器分析，最后将用户输入的内容划分为若干单元词汇，
        // 该词汇即会去从索引库中找域为title并且域对应的值与若干单元词汇匹配的文档对象id，
        // 最后找出一系列文档
        QueryParser queryParser1 = new QueryParser("title",analyzer);
        Query query1 = queryParser1.parse(q);
        QueryParser queryParser2 = new QueryParser("content",analyzer);
        Query query2 = queryParser2.parse(q);
        QueryParser queryParser3 = new QueryParser("keyWord",analyzer);
        Query query3 = queryParser3.parse(q);

        //放入查询条件到BooleanQuery中(把上面的查询放入到组合查询)
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        booleanQuery.add(query1, BooleanClause.Occur.SHOULD);
        booleanQuery.add(query2, BooleanClause.Occur.SHOULD);
        booleanQuery.add(query3, BooleanClause.Occur.SHOULD);

        //8.高亮搜索字【title】
        QueryScorer queryScorer1 = new QueryScorer(query1);
        //8.1创建span块
        Fragmenter fragmenter1 = new SimpleSpanFragmenter(queryScorer1);
        //8.2高亮哪一块
        SimpleHTMLFormatter simpleHTMLFormatter1 = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");
        //8.3格式完毕，开始高亮
        Highlighter highlighter1 = new Highlighter(simpleHTMLFormatter1,queryScorer1);
        highlighter1.setTextFragmenter(fragmenter1);


        //8.高亮搜索字【keyWord】
        QueryScorer queryScorer2 = new QueryScorer(query2);
        //8.1创建span块
        Fragmenter fragmenter2 = new SimpleSpanFragmenter(queryScorer2);
        //8.2高亮哪一块
        SimpleHTMLFormatter simpleHTMLFormatter2 = new SimpleHTMLFormatter("<b><font color='yellow'>", "</font></b>");
        //8.3格式完毕，开始高亮
        Highlighter highlighter2 = new Highlighter(simpleHTMLFormatter2,queryScorer2);
        highlighter2.setTextFragmenter(fragmenter2);

        //8.高亮搜索字【content】
        QueryScorer queryScorer3 = new QueryScorer(query3);
        //8.1创建span块
        Fragmenter fragmenter3 = new SimpleSpanFragmenter(queryScorer3);
        //8.2高亮哪一块
        SimpleHTMLFormatter simpleHTMLFormatter3 = new SimpleHTMLFormatter("<b><font color='blue'>", "</font></b>");
        //8.3格式完毕，开始高亮
        Highlighter highlighter3 = new Highlighter(simpleHTMLFormatter3,queryScorer3);
        highlighter3.setTextFragmenter(fragmenter3);


        //开始查询
        TopDocs topDocs = indexSearcher.search(booleanQuery.build(), 100);//最多返回100条数据

        //遍历查询结果，放入blogList,这些内容均是放在搜索博客首页的，需要高亮搜索词信息。（页面1）
        //通过下面的blogId可以直接打开链接，进而查看blog的详细信息。（页面2）
        for(ScoreDoc scoreDoc:topDocs.scoreDocs){
            Document document = indexSearcher.doc(scoreDoc.doc);
            Blog blog = new Blog();
            //通过id可以获取到整篇文章，从列表页面1--到--具体页面2的条件
            blog.setId(Integer.parseInt(document.get("id")));
            blog.setReleaseDateStr(document.get("releaseDate"));
            //下面三个需要高亮，另做处理,标题、关键字普通字符串，而内容是html字符串，需要转换。
            String title = document.get("title");
            String keyWord = document.get("keyWord");
            String content = StringEscapeUtils.escapeHtml(document.get("content"));
            //对上面3个内容进行高亮处理
            if (title != null){
                TokenStream tokenStream = analyzer.tokenStream("title", new StringReader(title));
                String hTitle = highlighter1.getBestFragment(tokenStream, title);
                if (StringUtil.isEmpty(hTitle)){
                    blog.setTitle(title);
                }else {
                    blog.setTitle(hTitle);
                }
            }
            if (keyWord != null){
                TokenStream tokenStream = analyzer.tokenStream("keyWord", new StringReader(keyWord));
                String hKeyWord = highlighter2.getBestFragment(tokenStream, keyWord);
                if (StringUtil.isEmpty(hKeyWord)){
                    blog.setKeyWord(keyWord);
                }else {
                    blog.setKeyWord(hKeyWord);
                }
            }
            if (content != null){
                TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(content));
                String hContent = highlighter3.getBestFragment(tokenStream, content);
                if (StringUtil.isEmpty(hContent)){
                    if (content.length() <= 200){
                        blog.setContent(content);
                    }else {
                        blog.setContent(content.substring(0,200));
                    }

                }else {
                    if (hContent.length() <= 200){
                        blog.setContent(hContent);
                        System.out.println("hContent<=200" + hContent);
                    }else {
                        blog.setContent(hContent.substring(0,200));
                        System.out.println("hContent>200" + hContent.substring(0,200));
                    }
                }
            }
            blogList.add(blog);
        }
        return blogList;
    }

}
