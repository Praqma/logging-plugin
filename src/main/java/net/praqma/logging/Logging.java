package net.praqma.logging;

import org.apache.oro.text.regex.PatternMatcher;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Logging {

    public static final String LOGFILENAME = "debug.log";
    public static final String POLLLOGPATH = "poll-logging";

    private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    private static final ThreadLocal<DateFormat> dateFormatNice = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("EEEE, d MMMMM yyyy");
        }
    };

    private static Logger logger = Logger.getLogger( Logging.class.getName() );

    public static class ComparePollLogs implements Comparator<PollLoggingFile> {
        @Override
        public int compare( PollLoggingFile o1, PollLoggingFile o2 ) {
            if( o1.number == o2.number ) {
                if( o1.date.before( o2.date ) ) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return o2.number - o1.number;
            }
        }
    }

    public static class PollLoggingFile {
        public File file;
        public String formattedDate;
        public Date date;
        public float kbytes;
        public String name;
        public int number;

        public String toString() {
            return file.getName() + ": " + date;
        }
    }

    private Logging() {}

    public static File[] getLogs( File logDir ) {
        return logDir.listFiles( new FilenameFilter() {
            public boolean accept( File file, String name ) {
                return name.endsWith( ".log" );
            }
        } );
    }

    public static Date getDate( File log ) {
        int l = log.getName().length();
        String date = log.getName().substring( l - 12, l - 4 );
        System.out.println( "DATE FOR " + log + ": " + date );
        try {
            return dateFormat.get().parse( date );
        } catch( Exception e ) {
            throw new IllegalStateException( "Unable to parse " + log.getName() + " for date." );
        }
    }

    private static final Pattern rx_logfile = Pattern.compile( "^(.+)-(\\d+)\\.(\\d+)\\.log$" );

    public static PollLoggingFile getPollLogFile( File log ) {
        Matcher m = rx_logfile.matcher( log.getName() );

        if( m.find() ) {
            try {
                PollLoggingFile f = new PollLoggingFile();

                f.file = log;
                f.date = dateFormat.get().parse( m.group( 3 ) );
                f.formattedDate = dateFormatNice.get().format( f.date );
                f.kbytes = Math.round( ( (float)log.length() / 1024f ) * 100f ) / 100f; // KB!
                f.name = m.group( 1 );
                f.number = Integer.parseInt( m.group( 2 ) );

                return f;
            } catch( Exception e ) {
                throw new IllegalStateException( log.getName() + " does not contain a date" );
            }
        } else {
            throw new IllegalStateException( log.getName() + " is not a log file" );
        }
    }

    public static List<PollLoggingFile> getPollLogs( File path ) {
        File[] logs = getLogs( path );
        List<PollLoggingFile> list = new LinkedList<PollLoggingFile>();

        for( File log : logs ) {
            try {
                list.add( getPollLogFile( log ) );
            } catch( Exception e ) {
                logger.warning( e.getMessage() );
            }
        }

        return list;
    }

    public static void prune( File[] logs, int days ) {
        if( logs.length > 1 && days > 0 ) {
            /* If there exists some logs, delete those older than seven days */
            Date seven = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add( Calendar.DATE, -1 * days );

            for( File log : logs ) {
                int l = log.getName().length();
                String date = log.getName().substring( l - 12, l - 4 );
                System.out.println( "DATE FOR " + log + ": " + date );
                try {
                    Date d = dateFormat.get().parse( date );
                    if( d.before( cal.getTime() ) ) {
                        if( !log.delete() ) {
                            logger.warning( "Unable to delete " + log );
                        }
                    }
                } catch( ParseException e ) {
                    logger.warning( "Unable to parse date: " + e.getMessage() );
                }
            }
        }
    }

    public static File getLogFile( File logDir, String name ) {
        System.out.println( "Getting log file: " + name );
        String d = dateFormat.get().format( new Date() );
        File logfile = new File( logDir, name + "." + d + ".log" );

        return logfile;
    }
}
