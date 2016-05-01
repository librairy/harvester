package org.librairy.harvester.file.services;

import com.google.common.base.Strings;
import java_cup.parser;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.librairy.harvester.file.parser.DefaultParser;
import org.librairy.harvester.file.parser.ParsedDocument;
import org.librairy.harvester.file.tokenizer.DefaultTokenizer;
import org.librairy.harvester.file.tokenizer.Language;
import org.librairy.model.domain.relations.Relation;
import org.librairy.model.domain.resources.Item;
import org.librairy.model.domain.resources.Resource;

import java.io.File;
import java.util.stream.Collectors;

/**
 * Created on 01/05/16:
 *
 * @author cbadenes
 */
public class ItemServiceTest {

    private DefaultParser parser;
    private DefaultTokenizer tokenizer;

    @Before
    public void setup(){
        this.parser = new DefaultParser();
        parser.setup();

        this.tokenizer = new DefaultTokenizer();
        tokenizer.setup();
    }

    @Test
    public void process(){



        // Parsing File of the Document
        File file = new File("/Users/cbadenes/Documents/OEG/Projects/Jorge-Coaching/Corpus/Coaching dialógico_ES.pdf");
        ParsedDocument parsedDocument = parser.parse(file);

        String fileName = StringUtils.substringBeforeLast(file.getName(), ".");
        Language language = fileName.endsWith("_ES")? Language.ES : Language.EN;

        // -> Textual Item
        String textualContent = parsedDocument.getText();
        if (!Strings.isNullOrEmpty(textualContent)){
            System.out.println("Language: " + language);
            Item textualItem = createItem(textualContent, "text", file.getAbsolutePath(),language);
            System.out.println(textualItem.getTokens());
        }

    }

    private Item createItem(String content, String type, String url, Language language){
        Item item = Resource.newItem();
        item.setFormat(type);
        item.setUrl(url);
        item.setContent(content);
        String tokens = tokenizer.tokenize(item.getContent(),language).stream().
                filter(token -> token.isValid()).
                map(token -> token.getLemma()).
                collect(Collectors.joining(" "));
        item.setTokens(tokens);
        return item;
    }
}
