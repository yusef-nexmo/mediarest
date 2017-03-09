package com.nexmo.mediarest;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.nexmo.mediarest.demo.DummyAuthResourceJWT;
import com.nexmo.mediarest.demo.DummyAuthResourcePassword;
import com.nexmo.mediarest.endpoints.MediaFilesResource;
import com.nexmo.restsvc.AbstractApplication;

import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;

public class MediaServiceApplication extends AbstractApplication<MediaServiceConfig> {

    public static void main(final String[] args) throws Exception {
        new MediaServiceApplication().run(args);
    }

    @Override
    protected void setUpResources(MediaServiceConfig cfg, Environment env) {
        JerseyEnvironment envJersey = env.jersey();
        envJersey.register(MultiPartFeature.class);
        envJersey.register(new MediaFilesResource());
        envJersey.register(new DummyAuthResourceJWT());
        envJersey.register(new DummyAuthResourcePassword());
        setupSwagger("1", getClass().getPackage().getName()+".endpoints");
    }
}