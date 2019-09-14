package com.itheima.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

public class FirstLucene {

    /**
     * 创建索引，增
     */
    @Test
    public void testIndex() throws Exception{
        //第一步：创建一个java工程，并导入jar包
        //第二步：创建一个indexWriter对象,directory存放索引库。
        //1.指定索引库的存放位置Directory对象
        //2.指定一个分析器，对文档进行分析
        Directory directory = FSDirectory.open(Paths.get("D:\\zl"));//保存索引到磁盘硬盘中。
//        Directory directory = new RAMDirectory();//保存索引到内存中。
        Analyzer analyzer =  new StandardAnalyzer();//官方推荐的分析器
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);

        //获取到需要进行查询的文档库。
        File f = new File("D:\\Lucene");
        File[] listFiles = f.listFiles();
        //一个个文档进行遍历，建立对应索引
        for (File file:listFiles) {
            //第三步：创建document对象
            Document document = new Document();

            //文件名称
            String fileName = file.getName();
            Field fileNameField = new TextField("fileName",fileName, Field.Store.YES);
            //文件大小
            long fileSize = FileUtils.sizeOf(file);
            Field fileSizeField = new LongField("fileSize",fileSize, Field.Store.YES);
            //文件路径
            String filePath = file.getPath();
            Field filePathField = new StoredField("filePath",filePath);
            //文件内容
            String fileContent = FileUtils.readFileToString(file);
            Field fileContentField = new TextField("fileContent",fileContent, Field.Store.YES);

            //第四步：创建field对象，将field对象添加到document对象中.
            document.add(fileNameField);
            document.add(fileSizeField);
            document.add(filePathField);
            document.add(fileContentField);

            //第五步：使用indexWriter对象将document对象写入索引库，此过程进行索引创建，并将索引和document对象写入索引库。
            indexWriter.addDocument(document);
        }
        //第六步：关闭IndexWriter对象。
        indexWriter.close();
    }




    //搜索索引（查询，当然这里的查询并不是查索引，而是通过索引去查对应的文档内容匹配，从而得到对应的文档的编号，进而得到对应的文档对象）
    //测试通过索引进行搜索相应的文档
    @Test
    public void testSearch() throws Exception{
        //第一步：创建一个Directory对象，也就是索引库存放的位置。
        Directory directory = FSDirectory.open(Paths.get("D:\\zl"));
        //第二步：创建一个indexReader对象，需要指定Directory对象。
        IndexReader indexReader = DirectoryReader.open(directory);
        //第三步：创建一个indexSearch对象，需要指定indexReader对象。
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //第四步：创建一个TermQuery对象，指定查询的域和查询的关键词。
        Query termQuery = new TermQuery(new Term("fileName","spring.txt"));
        //第五步：执行查询。
        TopDocs topDocs = indexSearcher.search(termQuery, 2);//返回头2个文档们
        //第六步：返回查询结果，遍历查询结果并输出。
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;//经过评分之后的文档拿回来。
        //遍历这些符合要求的评分后的文档对象们
        for (ScoreDoc scoreDoc:scoreDocs){
            int doc = scoreDoc.doc;//拿到各个文档对象中存储的文档id（文档编号）
            Document document = indexSearcher.doc(doc);//通过文档编号（Id）拿到对应的文档对象
            //文件名称
            String fileName = document.get("fileName");
            System.out.println(fileName);
            String fileSize = document.get("fileSize");
            System.out.println(fileSize);
            String filePath = document.get("filePath");
            System.out.println(filePath);
            String fileContent = document.get("fileContent");
            System.out.println(fileContent);
            System.out.println("==================================");
        }
        //第七步：关闭IndexReader对象
        indexReader.close();
    }

}
