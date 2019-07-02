/**
 * 上海中赢金融信息服务有限公司
 * Copyright (c) 2017-2027 Chinazyjr,Inc.All Rights Reserved.
 */

package com.tianta.tlib.zookeeper.utils;
/**
 * <b>ClassName:</b> PathUtils.java <br/>
 * <b>Description:</b> TODO <br/>
 * <b>Date:</b>     2016年4月15日 下午3:33:09<br/> 
 * @author   mobing
 * @version  	 
 */
public class PathUtils {
    /** validate the provided znode path string
     * @param path znode path string
     * @param isSequential if the path is being created
     * with a sequential flag
     * @throws IllegalArgumentException if the path is invalid
     */
    public static void validatePath(String path, boolean isSequential)
            throws IllegalArgumentException {
        validatePath(isSequential? path + "1": path);
    }

    /**
     * Validate the provided znode path string
     * @param path znode path string
     * @return The given path if it was valid, for fluent chaining
     * @throws IllegalArgumentException if the path is invalid
     */
    public static String validatePath(String path) throws IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (path.length() == 0) {
            throw new IllegalArgumentException("Path length must be > 0");
        }
        if (path.charAt(0) != '/') {
            throw new IllegalArgumentException(
                    "Path must start with / character");
        }
        if (path.length() == 1) { // done checking - it's the root
            return path;
        }
        if (path.charAt(path.length() - 1) == '/') {
            throw new IllegalArgumentException(
                    "Path must not end with / character");
        }

        String reason = null;
        char lastc = '/';
        char chars[] = path.toCharArray();
        char c;
        for (int i = 1; i < chars.length; lastc = chars[i], i++) {
            c = chars[i];

            if (c == 0) {
                reason = "null character not allowed @" + i;
                break;
            } else if (c == '/' && lastc == '/') {
                reason = "empty node name specified @" + i;
                break;
            } else if (c == '.' && lastc == '.') {
                if (chars[i-2] == '/' &&
                        ((i + 1 == chars.length)
                                || chars[i+1] == '/')) {
                    reason = "relative paths not allowed @" + i;
                    break;
                }
            } else if (c == '.') {
                if (chars[i-1] == '/' &&
                        ((i + 1 == chars.length)
                                || chars[i+1] == '/')) {
                    reason = "relative paths not allowed @" + i;
                    break;
                }
            } else if (c > '\u0000' && c < '\u001f'
                    || c > '\u007f' && c < '\u009F'
                    || c > '\ud800' && c < '\uf8ff'
                    || c > '\ufff0' && c < '\uffff') {
                reason = "invalid charater @" + i;
                break;
            }
        }

        if (reason != null) {
            throw new IllegalArgumentException(
                    "Invalid path string \"" + path + "\" caused by " + reason);
        }

        return path;
    }
}

