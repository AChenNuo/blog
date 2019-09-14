package com.itheima.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import javax.management.Query;
import java.nio.file.Paths;

public class TestDeleteUpdate {

    //获取写入索引库的写入对象IndexWriter
    public IndexWriter getIndexWriter() throws Exception{
        Directory directory = FSDirectory.open(Paths.get("D:\\zl"));//保存索引到磁盘硬盘中。
        Analyzer analyzer =  new StandardAnalyzer();//官方推荐的分析器
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        return new IndexWriter(directory,indexWriterConfig);
    }

    //删除全部索引
    @Test
    public void deleteAll()throws Exception{
        IndexWriter indexWriter = getIndexWriter();
        //删除全部索引
        indexWriter.deleteAll();
        //关闭indexWriter
        indexWriter.close();
    }

    //指定查询条件删除索引
    @Test
    public void deleteIndexByQuery() throws Exception{
        IndexWriter indexWriter = getIndexWriter();
        //创建一个查询条件
        TermQuery termQuery = new TermQuery(new Term("fileName","spring.txt"));
        //根据查询条件删除
        indexWriter.deleteDocuments(termQuery);
        //关闭indexWriter
        indexWriter.close();

    }

    //修改索引库
    @Test
    public void updateIndex() throws Exception {
        IndexWriter indexWriter = getIndexWriter();
        //创建一个Document对象
        Document document = new Document();
        //向document对象中添加域。
        //不同的document可以有不同的域，同一个document可以有相同的域。
        document.add(new TextField("filename", "要更新的文档", Field.Store.YES));
        document.add(new TextField("content", "2013年11月18日 - Lucene 简介 Lucene 是一个基于 Java 的全文信息检索工具包,它不是一个完整的搜索应用程序,而是为你的应用程序提供索引和搜索功能。", Field.Store.YES));
        indexWriter.updateDocument(new Term("content", "java"), document);
        //关闭indexWriter
        indexWriter.close();
    }
}
