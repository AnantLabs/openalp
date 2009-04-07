package net.openalp.oo.plugin;

import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;


public final class openALP extends WeakBase
   implements com.sun.star.lang.XServiceInfo,
              com.sun.star.linguistic2.XProofreader
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = openALP.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.linguistic2.Proofreader" };


    public openALP( XComponentContext context )
    {
        m_xContext = context;
    };

    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = Factory.createComponentFactory(openALP.class, m_serviceNames);
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                                                m_serviceNames,
                                                xRegistryKey);
    }

    // com.sun.star.lang.XServiceInfo:
    public String getImplementationName() {
         return m_implementationName;
    }

    public boolean supportsService( String sService ) {
        int len = m_serviceNames.length;

        for( int i=0; i < len; i++) {
            if (sService.equals(m_serviceNames[i]))
                return true;
        }
        return false;
    }

    public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }

    // com.sun.star.linguistic2.XSupportedLocales:
    public com.sun.star.lang.Locale[] getLocales()
    {
        // TODO: Exchange the default return implementation for "getLocales" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return new com.sun.star.lang.Locale[0];
    }

    public boolean hasLocale(com.sun.star.lang.Locale aLocale)
    {
        // TODO: Exchange the default return implementation for "hasLocale" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return false;
    }

    // com.sun.star.linguistic2.XProofreader:
    public boolean isSpellChecker()
    {
        // TODO: Exchange the default return implementation for "isSpellChecker" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return false;
    }

    public com.sun.star.linguistic2.ProofreadingResult doProofreading(String aDocumentIdentifier, String aText, com.sun.star.lang.Locale aLocale, int nStartOfSentencePosition, int nSuggestedBehindEndOfSentencePosition, com.sun.star.beans.PropertyValue[] aProperties) throws com.sun.star.lang.IllegalArgumentException
    {
        // TODO: Exchange the default return implementation for "doProofreading" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return new com.sun.star.linguistic2.ProofreadingResult();
    }

    public void ignoreRule(String aRuleIdentifier, com.sun.star.lang.Locale aLocale) throws com.sun.star.lang.IllegalArgumentException
    {
        // TODO: Insert your implementation for "ignoreRule" here.
    }

    public void resetIgnoreRules()
    {
        // TODO: Insert your implementation for "resetIgnoreRules" here.
    }

}
