/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component.strategy;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.librairy.harvester.component.GenericFile;
import org.apache.camel.util.ObjectHelper;

public class GenericFileExpressionRenamer<T> implements GenericFileRenamer<T> {
    private Expression expression;

    public GenericFileExpressionRenamer() {
    }

    public GenericFileExpressionRenamer(Expression expression) {
        this.expression = expression;
    }

    public GenericFile<T> renameFile(Exchange exchange, GenericFile<T> file) {
        ObjectHelper.notNull(expression, "expression");

        String newName = expression.evaluate(exchange, String.class);

        // make a copy as result and change its file name
        GenericFile<T> result = file.copyFrom(file);
        result.changeFileName(newName);
        return result;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }    
}
