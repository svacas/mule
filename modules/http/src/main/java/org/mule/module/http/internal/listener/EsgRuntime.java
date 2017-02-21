/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.http.internal.listener;


import org.mule.module.http.internal.ZipUtils;
import org.mule.util.FileUtils;
import org.mule.util.UUID;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class EsgRuntime
{


    public static final String ESG_HOME = "/opt/esg/current";
    public static final String APP_VERSION = "20170215-194739_adf1487210580628";
    public static final String APP_NAME = "pki-traffic";
    private String APP_HOME = ESG_HOME + "/userdir/global/conf/user/test/app/";
    private static final String ESG_CMD = System.getProperty("mule.esg.cmd", ESG_HOME + "/runtime/as7/bin/WorkflowTest -X /root/deploy -p /tmp/esg_log");
    private Process exec = null;

    private EsgConfigProperties properties = new EsgConfigProperties(8080, 8081, new RateLimitSettings(Integer.MAX_VALUE));

    private static final EsgRuntime instance = new EsgRuntime();

    private EsgRuntime()
    {
    }

    public static EsgRuntime getInstance()
    {
        return instance;
    }

    public EsgConfigProperties getProperties()
    {
        return properties;
    }

    public void setup(EsgConfigProperties properties)
    {
        this.properties = properties;
    }


    private void start()
    {
        if (exec == null)
        {
            try
            {
                System.out.format("---->ESG Starting ESG  %s...\n", ESG_CMD);
                deployEsgApp();
                exec = Runtime.getRuntime().exec(ESG_CMD);
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            BufferedReader br = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
                            String line;
                            while ((line = br.readLine()) != null)
                            {
                                System.out.println("-------> ESG - ERROR : "+line);
                            }
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();

                System.out.format("----->ESG Started ESG  %s...\n", ESG_CMD);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        else
        {
            System.out.println("ESG Already started");
        }
    }

    private void deployEsgApp()
    {
        File targetApp = new File(new File(APP_HOME), APP_NAME);
        if (targetApp.exists())
        {
            try
            {
                FileUtils.deleteDirectory(targetApp);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {

            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(APP_NAME + ".zip");
            File workingDirectory = new File(FileUtils.getTempDirectory(), UUID.getUUID());
            workingDirectory.mkdirs();

            ZipUtils.extract(resourceAsStream, workingDirectory);

            final HashMap<String, Object> velocityContext = new HashMap<>();
            velocityContext.put("PORT", properties.getPort());
            velocityContext.put("INTERNAL_PORT", properties.getInternalPort());
            velocityContext.put("RATE_LIMIT", properties.getRateLimitSettings().getLimit());

            createIngressConfigFile(workingDirectory, velocityContext);

            createAgentConfigFile(workingDirectory, velocityContext);

            createRateLimitConfigFile(workingDirectory, velocityContext);


            FileUtils.copyDirectory(workingDirectory, new File(APP_HOME));

            System.out.println("ESG ---> workingDirectory = " + workingDirectory);

            //Delete working directory
           // FileUtils.deleteDirectory(workingDirectory);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void createIngressConfigFile(File workingDirectory, HashMap<String, Object> velocityContext) throws IOException, ParseErrorException, MethodInvocationException, ResourceNotFoundException
    {
        final File ingressConfigTemplate = new File(workingDirectory, APP_NAME + "/" + APP_VERSION + "/settings/ISERV/pki-qos-traffic-Process-Receive-is1.vm");
        final File resultIngressConfig = new File(ingressConfigTemplate.getParentFile(), "pki-qos-traffic-Process-Receive-is1.xml");
        FileWriter resultWriter = new FileWriter(resultIngressConfig);
        Velocity.evaluate(new VelocityContext(velocityContext), resultWriter, "INGRESS_CONFIG", new FileReader(ingressConfigTemplate));
        resultWriter.close();
        FileUtils.deleteQuietly(ingressConfigTemplate);
    }

    private void createAgentConfigFile(File workingDirectory, HashMap<String, Object> velocityContext) throws IOException, ParseErrorException, MethodInvocationException, ResourceNotFoundException
    {
        final File agentConfigTemplate = new File(workingDirectory, APP_NAME + "/" + APP_VERSION + "/settings/IAGENT/pki-qos-traffic-Process-Invoke-ia1.vm");
        final File resultAgentConfig = new File(agentConfigTemplate.getParentFile(), "pki-qos-traffic-Process-Invoke-ia1.xml");
        FileWriter agentResult = new FileWriter(resultAgentConfig);
        Velocity.evaluate(new VelocityContext(velocityContext), agentResult, "AGENT_CONFIG", new FileReader(agentConfigTemplate));
        agentResult.close();
        FileUtils.deleteQuietly(agentConfigTemplate);
    }

    private void createRateLimitConfigFile(File workingDirectory, HashMap<String, Object> velocityContext) throws IOException, ParseErrorException, MethodInvocationException, ResourceNotFoundException
    {
        final File rateLimitConfigTemplate = new File(workingDirectory, APP_NAME + "/" + APP_VERSION + "/appBundle/qos/Policy.vm");
        final File rateLimitAgentConfig = new File(rateLimitConfigTemplate.getParentFile(), "Policy.qos");
        FileWriter rateLimitResult = new FileWriter(rateLimitAgentConfig);
        Velocity.evaluate(new VelocityContext(velocityContext), rateLimitResult, "POLICY_CONFIG", new FileReader(rateLimitConfigTemplate));
        rateLimitResult.close();
        FileUtils.deleteQuietly(rateLimitConfigTemplate);
    }

    public void stop()
    {
        if (exec != null)
        {
            exec.destroy();
            exec = null;
        }
        else
        {
            System.out.println("ESG Wast Not  started");
        }
    }

    public void apply()
    {
        stop();
        start();
    }


    public static class EsgConfigProperties
    {

        private int port;
        private int internalPort;

        private RateLimitSettings rateLimitSettings;

        public EsgConfigProperties(int port, int internalPort, RateLimitSettings rateLimitSettings)
        {
            this.port = port;
            this.internalPort = internalPort;
            this.rateLimitSettings = rateLimitSettings;
        }

        public int getPort()
        {
            return port;
        }

        public int getInternalPort()
        {
            return internalPort;
        }

        public RateLimitSettings getRateLimitSettings()
        {
            return rateLimitSettings;
        }

        public void setRateLimitSettings(RateLimitSettings rateLimitSettings)
        {
            this.rateLimitSettings = rateLimitSettings;
        }
    }


    public  static class RateLimitSettings {
        private int limit;

        public RateLimitSettings(int limit)
        {
            this.limit = limit;
        }

        public int getLimit()
        {
            return limit;
        }
    }
}
