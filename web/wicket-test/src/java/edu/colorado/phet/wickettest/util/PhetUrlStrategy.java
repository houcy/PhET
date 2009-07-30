package edu.colorado.phet.wickettest.util;

import java.util.Locale;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.PageParameters;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class PhetUrlStrategy implements IRequestTargetUrlCodingStrategy {
    private Locale locale;
    private String localeString;
    private PhetUrlMapper mapper;

    public PhetUrlStrategy( Locale locale, PhetUrlMapper mapper ) {
        this.mapper = mapper;
        this.locale = locale;
        this.localeString = LocaleUtils.localeToString( locale );
    }

    public String getMountPath() {
        return localeString;
    }

    public CharSequence encode( IRequestTarget request ) {
        if ( request instanceof BookmarkablePageRequestTarget ) {
            BookmarkablePageRequestTarget bookRequest = (BookmarkablePageRequestTarget) request;
            // TODO
        }
        throw new RuntimeException( "PhetUrlStrategy.encode" );
    }

    public IRequestTarget decode( RequestParameters requestParameters ) {
        System.out.println( "X decode( RequestParameters ): " + requestParameters );
        System.out.println( "X Path: " + requestParameters.getPath() );
        System.out.println( "X ComponentPath: " + requestParameters.getComponentPath() );
        PageParameters params = new PageParameters( requestParameters.getParameters() );
        params.add( "path", requestParameters.getPath() );
        params.add( "localeString", localeString );
        params.put( "locale", LocaleUtils.stringToLocale( localeString ) );
        String strippedPath = stripPath( requestParameters.getPath() );
        Class toClass = mapper.getMappedClass( strippedPath, params );
        return new BookmarkablePageRequestTarget( toClass, params );
    }

    private String stripPath( String path ) {
        if ( path.startsWith( "/" + localeString ) ) {
            return path.substring( localeString.length() + 2 );
        }
        else if ( path.startsWith( localeString ) ) {
            return path.substring( localeString.length() + 1 );
        }
        else {
            return path;
        }
    }

    public boolean matches( IRequestTarget target ) {
        return mapper.containsClass( target.getClass() );
    }

    public boolean matches( String str ) {
        Class clazz = mapper.getMappedClass( stripPath( str ) );
        boolean ret = clazz != null;
        System.out.println( " XMatches? : " + str + " = " + ret + ( ret ? " for " + clazz.getCanonicalName() : "" ) );
        return ret;
    }
}
