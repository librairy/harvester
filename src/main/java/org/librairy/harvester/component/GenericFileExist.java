/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component;

/**
 * Represent the kinds of options when writing a file and what to do in case of an existing file exists.
 *
 * @version 
 */
public enum GenericFileExist {

    Override, Append, Fail, Ignore, Move, TryRename
}
