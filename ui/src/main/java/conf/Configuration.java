package conf;

import java.util.Map.Entry;

import javax.inject.Inject;

import ninja.utils.NinjaProperties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

class Configuration extends AbstractModule {

    private final NinjaProperties properties;

    @Inject
    public Configuration(final NinjaProperties properties) {
	this.properties = properties;
    }

    @Override
    protected void configure() {
	for (final Entry<Object, Object> property : properties.getAllCurrentNinjaProperties().entrySet()) {
	    Named name = Names.named(String.valueOf(property.getKey()));
	    String val = String.valueOf(property.getValue());
	    bindConstant().annotatedWith(name).to(val);
	}
    }

}
