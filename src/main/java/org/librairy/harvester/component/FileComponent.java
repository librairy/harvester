/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ComponentConfiguration;
import org.apache.camel.spi.EndpointCompleter;
import org.apache.camel.util.FileUtil;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.StringHelper;

/**
 *  * The <a href="http://camel.apache.org/file.html">File Component</a> provides access to file systems.
 */
public class FileComponent extends GenericFileComponent<File> implements EndpointCompleter {
    /**
     * GenericFile property on Camel Exchanges.
     */
    public static final String FILE_EXCHANGE_FILE = "CamelFileExchangeFile";
    
    /**
     * Default camel lock filename postfix
     */
    public static final String DEFAULT_LOCK_FILE_POSTFIX = ".camelLock";

    public FileComponent() {
        setEndpointClass(FileEndpoint.class);
    }

    public FileComponent(CamelContext context) {
        super(context);
        setEndpointClass(FileEndpoint.class);
    }

    protected GenericFileEndpoint<File> buildFileEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        // the starting directory must be a static (not containing dynamic expressions)
        if (StringHelper.hasStartToken(remaining, "simple")) {
            throw new IllegalArgumentException("Invalid directory: " + remaining
                    + ". Dynamic expressions with ${ } placeholders is not allowed."
                    + " Use the fileName option to set the dynamic expression.");
        }

        File file = new File(remaining);

        FileEndpoint result = new FileEndpoint(uri, this);
        result.setFile(file);

        GenericFileConfiguration config = new GenericFileConfiguration();
        config.setDirectory(FileUtil.isAbsolute(file) ? file.getAbsolutePath() : file.getPath());
        result.setConfiguration(config);

        return result;
    }

    protected void afterPropertiesSet(GenericFileEndpoint<File> endpoint) throws Exception {
        // noop
    }

    public List<String> completeEndpointPath(ComponentConfiguration configuration, String completionText) {
        boolean empty = ObjectHelper.isEmpty(completionText);
        String pattern = completionText;
        File file = new File(completionText);
        String prefix = completionText;
        if (file.exists()) {
            pattern = "";
        } else {
            String startPath = ".";
            if (!empty) {
                int idx = completionText.lastIndexOf('/');
                if (idx >= 0) {
                    startPath = completionText.substring(0, idx);
                    if (startPath.length() == 0) {
                        startPath = "/";
                    }
                    pattern = completionText.substring(idx + 1);
                }
            }
            file = new File(startPath);
            prefix = startPath;
        }
        if (prefix.length() > 0 && !prefix.endsWith("/")) {
            prefix += "/";
        }
        if (prefix.equals("./")) {
            prefix = "";
        }
        File[] list = file.listFiles();
        List<String> answer = new ArrayList<String>();
        for (File aFile : list) {
            String name = aFile.getName();
            if (pattern.length() == 0 || name.contains(pattern)) {
                if (isValidEndpointCompletion(configuration, completionText, aFile)) {
                    answer.add(prefix + name);
                }
            }
        }
        return answer;
    }

    /**
     * Returns true if this is a valid file for completion. By default we should ignore files that start with a "."
     */
    protected boolean isValidEndpointCompletion(ComponentConfiguration configuration, String completionText,
                                           File file) {
        return !file.getName().startsWith(".");
    }
}
