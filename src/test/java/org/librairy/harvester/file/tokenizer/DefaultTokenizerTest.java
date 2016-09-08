package org.librairy.harvester.file.tokenizer;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.librairy.harvester.file.descriptor.FileDescription;
import org.librairy.harvester.file.descriptor.pdf.PdfDescriptor;
import org.librairy.harvester.file.parser.ParsedDocument;
import org.librairy.harvester.file.parser.pdf.PDFParser;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.sun.tools.doclets.formats.html.markup.HtmlStyle.description;

/**
 * Created on 01/05/16:
 *
 * @author cbadenes
 */
public class DefaultTokenizerTest {


    @Test
    public void tokenize(){
        DefaultTokenizer tokenizer = new DefaultTokenizer();
        tokenizer.setup();

        String text = "En estos momentos el mundo gira más rápido que nunca, el cambio es una constante en nuestra vida.";
        List<Token> tokens = tokenizer.tokenize(text,Language.ES);

        System.out.println(tokens);

        List<Token> filtered = tokens.stream().filter(token -> token.isValid()).collect(Collectors.toList());
        System.out.println(filtered);


    }


    @Test
    public void identifyLanguage(){
        File file = new File("/User/cbadenes/sample/p-kasda0012.pdf");

        String fileName = StringUtils.substringBeforeLast(file.getName(), ".");
        Language language = fileName.endsWith("_ES")? Language.ES : Language.EN;

        Assert.assertEquals(Language.EN,language);


        File file2 = new File("/User/cbadenes/sample/p-kasda0012_ES.pdf");

        String fileName2 = StringUtils.substringBeforeLast(file2.getName(), ".");
        Language language2 = fileName2.endsWith("_ES")? Language.ES : Language.EN;
        Assert.assertEquals(Language.ES,language2);
    }

    @Test
    public void tokenizePdf(){
        File file = new File("./src/test/resources/files/default/siggraph/a99-kopf.pdf");

        PDFParser pdfParser = new PDFParser();

        ParsedDocument document = pdfParser.parse(file);


        DefaultTokenizer tokenizer = new DefaultTokenizer();
        tokenizer.setup();

        tokenizer.tokenize(document.getText(), Language.EN);

    }
}
