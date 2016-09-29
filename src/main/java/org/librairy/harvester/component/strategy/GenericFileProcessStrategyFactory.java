/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */
package org.librairy.harvester.component.strategy;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Expression;
import org.librairy.harvester.component.GenericFileExclusiveReadLockStrategy;
import org.librairy.harvester.component.GenericFileProcessStrategy;
import org.apache.camel.util.ObjectHelper;

public final class GenericFileProcessStrategyFactory {

    private GenericFileProcessStrategyFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <T> GenericFileProcessStrategy<T> createGenericFileProcessStrategy(CamelContext context, Map<String, Object> params) {

        // We assume a value is present only if its value not null for String and 'true' for boolean
        Expression moveExpression = (Expression) params.get("move");
        Expression moveFailedExpression = (Expression) params.get("moveFailed");
        Expression preMoveExpression = (Expression) params.get("preMove");
        boolean isNoop = params.get("noop") != null;
        boolean isDelete = params.get("delete") != null;
        boolean isMove = moveExpression != null || preMoveExpression != null || moveFailedExpression != null;

        if (isDelete) {
            GenericFileDeleteProcessStrategy<T> strategy = new GenericFileDeleteProcessStrategy<T>();
            strategy.setExclusiveReadLockStrategy((GenericFileExclusiveReadLockStrategy<T>) getExclusiveReadLockStrategy(params));
            if (preMoveExpression != null) {
                GenericFileExpressionRenamer<T> renamer = new GenericFileExpressionRenamer<T>();
                renamer.setExpression(preMoveExpression);
                strategy.setBeginRenamer(renamer);
            }
            if (moveFailedExpression != null) {
                GenericFileExpressionRenamer<T> renamer = new GenericFileExpressionRenamer<T>();
                renamer.setExpression(moveFailedExpression);
                strategy.setFailureRenamer(renamer);
            }
            return strategy;
        } else if (isMove || isNoop) {
            GenericFileRenameProcessStrategy<T> strategy = new GenericFileRenameProcessStrategy<T>();
            strategy.setExclusiveReadLockStrategy((GenericFileExclusiveReadLockStrategy<T>) getExclusiveReadLockStrategy(params));
            if (!isNoop && moveExpression != null) {
                // move on commit is only possible if not noop
                GenericFileExpressionRenamer<T> renamer = new GenericFileExpressionRenamer<T>();
                renamer.setExpression(moveExpression);
                strategy.setCommitRenamer(renamer);
            }
            // both move and noop supports pre move
            if (moveFailedExpression != null) {
                GenericFileExpressionRenamer<T> renamer = new GenericFileExpressionRenamer<T>();
                renamer.setExpression(moveFailedExpression);
                strategy.setFailureRenamer(renamer);
            }
            // both move and noop supports pre move
            if (preMoveExpression != null) {
                GenericFileExpressionRenamer<T> renamer = new GenericFileExpressionRenamer<T>();
                renamer.setExpression(preMoveExpression);
                strategy.setBeginRenamer(renamer);
            }
            return strategy;
        } else {
            // default strategy will do nothing
            GenericFileNoOpProcessStrategy<T> strategy = new GenericFileNoOpProcessStrategy<T>();
            strategy.setExclusiveReadLockStrategy((GenericFileExclusiveReadLockStrategy<T>) getExclusiveReadLockStrategy(params));
            return strategy;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> GenericFileExclusiveReadLockStrategy<T> getExclusiveReadLockStrategy(Map<String, Object> params) {
        GenericFileExclusiveReadLockStrategy<T> strategy = (GenericFileExclusiveReadLockStrategy<T>) params.get("exclusiveReadLockStrategy");
        if (strategy != null) {
            return strategy;
        }

        // no explicit strategy set then fallback to readLock option
        String readLock = (String) params.get("readLock");
        if (ObjectHelper.isNotEmpty(readLock)) {
            if ("none".equals(readLock) || "false".equals(readLock)) {
                return null;
            } else if ("rename".equals(readLock)) {
                GenericFileRenameExclusiveReadLockStrategy<T> readLockStrategy = new GenericFileRenameExclusiveReadLockStrategy<T>();
                Long timeout = (Long) params.get("readLockTimeout");
                if (timeout != null) {
                    readLockStrategy.setTimeout(timeout);
                }
                Boolean readLockMarkerFile = (Boolean) params.get("readLockMarkerFile");
                if (readLockMarkerFile != null) {
                    readLockStrategy.setMarkerFiler(readLockMarkerFile);
                }
                return readLockStrategy;
            }
        }
        return null;
    }
}
