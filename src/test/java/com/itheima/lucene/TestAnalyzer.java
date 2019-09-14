package com.itheima.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;

/**
 * 查看分析器的分词效果。
 */
public class TestAnalyzer {

    @Test
    public void testAnalyzer() throws IOException {
        //创建一个分析器对象
//        StandardAnalyzer analyzer = new StandardAnalyzer();//apache提供的标准分析器
//        CJKAnalyzer analyzer = new CJKAnalyzer();//俩俩分词，二分法分析器
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();//中国分析器，但无法禁用和扩展一些词汇
//        IKAnalyzer analyzer = new IKAnalyzer();//该IK已经不更新了，已经不支持Lucene5.x的版本了。
        //从分析器对象中获得tokenStream对象
        //参数1：域的名称，可以为null或者"" 参数2：要分析的文本内容
        TokenStream tokenStream = analyzer.tokenStream("","我们是中国人");
        //设置一个引用，引用可以有多重类型，可以是关键词的引用、偏移量的引用
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //偏移量
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        //调用tokenStream的reset方法
        tokenStream.reset();
        //使用while循环遍历单词列表
        while (tokenStream.incrementToken()){
            System.out.println("start->" + offsetAttribute.startOffset());
            //打印单词
            System.out.println(charTermAttribute);
            System.out.println("end->" + offsetAttribute.endOffset());
        }
        //关闭tokenStream
        tokenStream.close();
    }
}
