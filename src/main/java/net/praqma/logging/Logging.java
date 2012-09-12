package net.praqma.logging;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class Logging {

    public static final String LOGFILENAME = "debug.log";
    private static DateFormat dateFormatter = new SimpleDateFormat( "yyyyMMdd" );
    private static Logger logger = Logger.getLogger( Logging.class.getName() );

    private Logging() {}

    public static File[] getLogs( File logDir ) {
        return logDir.listFiles( new FilenameFilter() {
            public boolean accept( File file, String name ) {
                return name.endsWith( ".log" );
            }
        } );
    }

    public static void prune( File[] logs, int days ) {
        if( logs.length > 1 ) {
            /* If there exists some logs, delete those older than seven days */
            Date seven = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add( Calendar.DATE, -1 * days );

            for( File log : logs ) {
                int l = log.getName().length();
                String date = log.getName().substring( l - 12, l - 4 );
                System.out.println( "DATE FOR " + log + ": " + date );
                try {
                    Date d = dateFormatter.parse( date );
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
        String d = dateFormatter.format( new Date() );
        File logfile = new File( logDir, name + "." + d + ".log" );

        return logfile;
    }
}
