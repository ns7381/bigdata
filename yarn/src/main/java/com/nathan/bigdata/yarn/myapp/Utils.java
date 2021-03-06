package com.nathan.bigdata.yarn.myapp;

import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.ConverterUtils;

import java.io.IOException;
import java.util.Map;

public class Utils {
    public static final String YARNAPP_JAR_NAME = "YARNAPP.jar";
    public static final Path YARNAPP_JAR_PATH = new Path("/apps/" + YARNAPP_JAR_NAME);

    public static void setUpEnv(Map<String, String> env, YarnConfiguration conf) {
        StringBuilder classPathEnv = new StringBuilder(Environment.CLASSPATH.$$()).append(
                ApplicationConstants.CLASS_PATH_SEPARATOR).append("./*");
        for (String c : conf.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH,
                YarnConfiguration.DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH)) {
            classPathEnv.append(ApplicationConstants.CLASS_PATH_SEPARATOR);
            classPathEnv.append(c.trim());
        }
        classPathEnv.append(ApplicationConstants.CLASS_PATH_SEPARATOR).append("./log4j.properties");

        if (conf.getBoolean(YarnConfiguration.IS_MINI_YARN_CLUSTER, false)) {
            classPathEnv.append(':');
            classPathEnv.append(System.getProperty("java.class.path"));
        }
        env.put("CLASSPATH", classPathEnv.toString());
    }

    public static void setUpLocalResource(Path resPath, LocalResource res, YarnConfiguration conf) throws IOException {
        Path qPath = FileContext.getFileContext().makeQualified(resPath);

        FileStatus status = FileSystem.get(conf).getFileStatus(qPath);
        res.setResource(ConverterUtils.getYarnUrlFromPath(qPath));
        res.setSize(status.getLen());
        res.setTimestamp(status.getModificationTime());
        res.setType(LocalResourceType.FILE);
        res.setVisibility(LocalResourceVisibility.PUBLIC);
    }
}

