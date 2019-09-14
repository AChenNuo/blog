package com.itheima.lucene;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.nio.file.Paths;

public class TestQuery {

    //得到（通过索引来查询文档IndexSearcher对象）和从索引库读出文档对象的操作对象indexReader
    public IndexSearcher getIndexSearcher() throws Exception{
        Directory directory = FSDirectory.open(Paths.get("D:\\zl"));
        IndexReader indexReader = DirectoryReader.open(directory);
        return new IndexSearcher(indexReader);
    }

    //打印索引到的(匹配到的)文档相关内容
    public void printResult(IndexSearcher indexSearcher, Query query)throws Exception{
        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;//经过评分之后的文档拿回来。
        for (ScoreDoc scoreDoc:scoreDocs){
            int doc = scoreDoc.doc;//拿到各个文档对象中存储的文档id（文档编号）
            Document document = indexSearcher.doc(doc);//通过文档编号（Id）拿到对应的文档对象
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

    }

    //1.查询所有
    @Test
    public void testMatchAllDocsQuery()throws Exception{
        IndexSearcher indexSearcher = getIndexSearcher();

        MatchAllDocsQuery query = new MatchAllDocsQuery();

        printResult(indexSearcher,query);
        indexSearcher.getIndexReader().close();
    }

    //2.精准查询，FirstLucene中的testSearch正是使用了精准查询
    //Query termQuery = new TermQuery(new Term("fileName","spring.txt"));


    //3.根据数值范围查询
    @Test
    public void testNumericRangeQuery()throws Exception{
        IndexSearcher indexSearcher = getIndexSearcher();

        //创建数值范围查询条件
        NumericRangeQuery query = NumericRangeQuery.newLongRange("fileSize", 100L, 200L, true, true);

        printResult(indexSearcher,query);
        indexSearcher.getIndexReader().close();
    }

    //4.组合查询
    @Test
    public void testBooleanQuery()throws Exception{
        IndexSearcher indexSearcher = getIndexSearcher();

        //创建组合查询条件
        BooleanQuery booleanQuery = new BooleanQuery();
        TermQuery query1 = new TermQuery(new Term("fileContent", "spring"));
        TermQuery query2 = new TermQuery(new Term("fileContent", "springmvc"));
        booleanQuery.add(query1, BooleanClause.Occur.MUST);
        booleanQuery.add(query2, BooleanClause.Occur.MUST);

        printResult(indexSearcher,booleanQuery);
        indexSearcher.getIndexReader().close();
    }

//    =============================上面的四种均为query的子类查询，为一大类(上面没有用到分析器对用户输入的文字进行分析，然后再去查索引找对应内容)。
//    =============================下面为queryparse解析查询，为另一大类（具有分析器）。

    //5.解析查询
    //5.1条件解释的对象查询
    @Test
    public void testQueryParser() throws Exception{
        IndexSearcher indexSearcher = getIndexSearcher();

        //创建查询条件
        //参数1：默认查询的域
        //参数2：采用的分析器
        QueryParser queryParser = new QueryParser("fileName",new StandardAnalyzer());
//        Query query = (Query) queryParser.parse("*:*");//查询所有，域:值
//        Query query = queryParser.parse("spring.txt");//使用前面默认的域fileName,spring.txt则为索引值，通过索引值查到对应的文档对象等
//        Query query = queryParser.parse("fileContent:springmvc");
//        Query query = queryParser.parse("springmvc.txt spring.txt is good job");
        // + = MUST, - = NOT , 空 = SHOULD
        Query query = queryParser.parse("+fileName:spring.txt fileName:aaa.txt");

        printResult(indexSearcher,query);
        indexSearcher.getIndexReader().close();
    }

    //5.2数值范围查询，Lucene的queryParse是不支持的，solr该框架是可以支持的。
//    Query query = queryParser.parse("fileSize:{47 TO 200]");

    //5.3组合查询，支持
//    + = MUST, - = NOT , 空 = SHOULD ，，，第二种写法必须，AND
//    Query query = queryParser.parse("+fileName:spring.txt fileName:aaa.txt");


    //5.4多个默认域查询
    @Test
    public void testMultiFieldQueryParser()throws Exception{
        IndexSearcher indexSearcher = getIndexSearcher();

        //创建查询条件
        String[] fields = {"fileName","fileContent"};
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields,new StandardAnalyzer());
        Query query = multiFieldQueryParser.parse("spring is springmvc");//查询fileName是spring或者springmvc,或者fileContent是spring或者springmvc

        printResult(indexSearcher,query);
        indexSearcher.getIndexReader().close();
    }


}
