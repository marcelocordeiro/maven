package org.apache.maven.plugin.descriptor;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @todo these are all really tools for dealing with xml configurations so they
 * should be packaged as such.
 */
public class PluginDescriptorBuilder
{
    public PluginDescriptor build( Reader reader )
        throws Exception
    {
        PlexusConfiguration c = buildConfiguration( reader );

        PluginDescriptor pluginDescriptor = new PluginDescriptor();

        pluginDescriptor.setId( c.getChild( "id" ).getValue() );

        // ----------------------------------------------------------------------
        // Components
        // ----------------------------------------------------------------------

        PlexusConfiguration[] mojoConfigurations = c.getChild( "mojos" ).getChildren( "mojo" );

        List mojos = new ArrayList();

        for ( int i = 0; i < mojoConfigurations.length; i++ )
        {
            PlexusConfiguration component = mojoConfigurations[i];

            mojos.add( buildComponentDescriptor( component ) );
        }

        pluginDescriptor.setMojos( mojos );

        // ----------------------------------------------------------------------
        // Dependencies
        // ----------------------------------------------------------------------

        PlexusConfiguration[] dependencyConfigurations = c.getChild( "dependencies" ).getChildren( "dependency" );

        List dependencies = new ArrayList();

        for ( int i = 0; i < dependencyConfigurations.length; i++ )
        {
            PlexusConfiguration d = dependencyConfigurations[i];

            Dependency cd = new Dependency();

            cd.setArtifactId( d.getChild( "artifactId" ).getValue() );

            cd.setGroupId( d.getChild( "groupId" ).getValue() );

            cd.setType( d.getChild( "type" ).getValue() );

            cd.setVersion( d.getChild( "version" ).getValue() );

            dependencies.add( cd );
        }

        pluginDescriptor.setDependencies( dependencies );

        return pluginDescriptor;
    }

    public MojoDescriptor buildComponentDescriptor( PlexusConfiguration c )
        throws Exception
    {
        MojoDescriptor mojo = new MojoDescriptor();

        mojo.setId( c.getChild( "id" ).getValue() );

        mojo.setImplementation( c.getChild( "implementation" ).getValue() );

        mojo.setInstantiationStrategy( c.getChild( "instantiationStrategy" ).getValue() );

        mojo.setDescription( c.getChild( "description" ).getValue() );

        String dependencyResolution = c.getChild( "requiresDependencyResolution" ).getValue();

        if ( dependencyResolution != null )
        {
            mojo.setRequiresDependencyResolution( dependencyResolution.equals( "true" ) ? true : false );
        }

        // ----------------------------------------------------------------------
        // Parameters
        // ----------------------------------------------------------------------

        PlexusConfiguration[] parameterConfigurations = c.getChild( "parameters" ).getChildren( "parameter" );

        List parameters = new ArrayList();

        for ( int i = 0; i < parameterConfigurations.length; i++ )
        {
            PlexusConfiguration d = parameterConfigurations[i];

            Parameter cd = new Parameter();

            cd.setName( d.getChild( "name" ).getValue() );

            cd.setType( d.getChild( "type" ).getValue() );

            String s = c.getChild( "required" ).getValue();

            if ( s != null )
            {
                cd.setRequired( s.equals( "true" ) ? true : false );
            }

            cd.setValidator( d.getChild( "validator" ).getValue() );

            cd.setDescription( d.getChild( "description" ).getValue() );

            cd.setExpression( d.getChild( "expression" ).getValue() );

            parameters.add( cd );
        }

        mojo.setParameters( parameters );

        // ----------------------------------------------------------------------
        // Prereqs
        // ----------------------------------------------------------------------

        PlexusConfiguration[] prereqConfigurations = c.getChild( "prereqs" ).getChildren( "prereq" );

        List prereqs = new ArrayList();

        for ( int i = 0; i < prereqConfigurations.length; i++ )
        {
            PlexusConfiguration d = prereqConfigurations[i];

            prereqs.add( d.getValue() );
        }

        mojo.setPrereqs( prereqs );

        return mojo;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public PlexusConfiguration buildConfiguration( Reader configuration )
        throws Exception
    {
        return new XmlPlexusConfiguration( Xpp3DomBuilder.build( configuration ) );
    }
}
