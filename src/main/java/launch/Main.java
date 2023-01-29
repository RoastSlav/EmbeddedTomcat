package launch;

import Servlets.AuthenticationServlet;
import Servlets.CommentsServlet;
import Servlets.PostsServlet;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.cli.*;

import java.io.File;

public class Main {
    private static final String PROGRAM_NAME = "PostsAPI";
    private static final String DEFAULT_PORT = "8080";
    private static final HelpFormatter FORMATTER = new HelpFormatter();

    public static void main(String[] args) throws Exception {
        Options options = intializeOptions();
        CommandLineParser cmdParser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = cmdParser.parse(options, args);
        } catch (ParseException e) {
            FORMATTER.printHelp(PROGRAM_NAME, options, true);
            return;
        }

        if (cmd.hasOption("help")) {
            FORMATTER.printHelp(PROGRAM_NAME, options, true);
            return;
        }

        Tomcat tomcat = new Tomcat();

        String webPort = cmd.getOptionValue("p");
        if (webPort == null || webPort.isEmpty()) {
            webPort = DEFAULT_PORT;
        }

        try {
            int port = Integer.valueOf(webPort);
            tomcat.setPort(port);
        } catch (NumberFormatException e) {
            throw new RuntimeException("The port parameter accepts only valid numbers");
        }

        String t = cmd.getOptionValue("t");
        if (t != null && !t.isEmpty()) {
            int threadsNum;
            try {
                threadsNum = Integer.valueOf(t);
            } catch (NumberFormatException e) {
                throw new RuntimeException("The threads parameter accepts only valid numbers");
            }
            Connector connector = tomcat.getConnector();
            connector.setAttribute("maxThreads", threadsNum);
        }

        String contextPath = "/";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);


        tomcat.addServlet(context, "Post", new PostsServlet());
        context.addServletMappingDecoded("/posts", "Post");
        context.addServletMappingDecoded("/posts/*", "Post");
        context.addServletMappingDecoded("/posts/*/comments", "Post");

        tomcat.addServlet(context, "Comments", new CommentsServlet());
        context.addServletMappingDecoded("/comments/*", "Comments");

        tomcat.addServlet(context, "Auth", new AuthenticationServlet());
        context.addServletMappingDecoded("/Login", "Auth");
        context.addServletMappingDecoded("/Register", "Auth");

        tomcat.start();
        tomcat.getServer().await();
    }

    private static Options intializeOptions() {
        Options options = new Options();
        Option port = Option.builder().option("p").argName("port").hasArg().desc("The port for the server").build();
        options.addOption(port);
        Option threads = Option.builder().option("t").argName("threads").hasArg().desc("The number of threads the server should use").build();
        options.addOption(threads);
        return options;
    }
}