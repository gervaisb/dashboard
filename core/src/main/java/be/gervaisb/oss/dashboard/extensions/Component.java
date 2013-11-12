package be.gervaisb.oss.dashboard.extensions;

import java.util.Map;

public interface Component {

    String render(Map<String, Object> model);

}
