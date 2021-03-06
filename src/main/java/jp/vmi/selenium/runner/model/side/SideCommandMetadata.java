package jp.vmi.selenium.runner.model.side;

import java.util.HashMap;
import java.util.Map;

import jp.vmi.selenium.runner.model.ArgType;
import jp.vmi.selenium.runner.model.ICommandMetadata;
import jp.vmi.selenium.runner.model.ICommandSignature;
import jp.vmi.selenium.runner.model.utils.CommandJs;

/**
 * Side command metadata.
 */
public class SideCommandMetadata implements ICommandMetadata {

    private static final SideCommandMetadata METADATA = new SideCommandMetadata();

    /**
     * Get instance.
     *
     * @return command metadata instance.
     */
    public static SideCommandMetadata getInstance() {
        return METADATA;
    }

    private final Map<String, SideCommandSignature> commandSignatureMap = new HashMap<>();

    private SideCommandMetadata() {
        CommandJs commandJs = CommandJs.load();
        commandJs.getCommandList().forEach((id, map) -> {
            String name = map.get("name");
            String description = map.get("description");
            String target = map.get("target");
            String value = map.get("value");
            SideCommandSignature info = new SideCommandSignature(id, name, description, ArgType.lookup(target), ArgType.lookup(value));
            commandSignatureMap.put(id, info);
        });
    }

    @Override
    public ICommandSignature getSignature(String id) {
        return commandSignatureMap.get(id);
    }
}
