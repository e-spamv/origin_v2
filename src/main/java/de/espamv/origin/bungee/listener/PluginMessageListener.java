package de.claved.origin.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.claved.origin.bungee.api.manager.OriginManager;
import de.claved.origin.bungee.api.manager.TablistManager;
import de.claved.origin.bungee.api.OriginPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PluginMessageListener implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        String tag = event.getTag();
        byte[] data = event.getData();

        if (tag.equals("origin")) {
            ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(data);
            String subChannel = byteArrayDataInput.readUTF();
            UUID uuid = UUID.fromString(byteArrayDataInput.readUTF());
            OriginPlayer player = OriginManager.getInstance().getPlayer(uuid);

            switch (subChannel) {
                case "updateLanguage":
                    player.updateLanguage();
                    player.setTabHeader(TablistManager.getInstance().getHeader(player), TablistManager.getInstance().getFooter(player));
                    break;
                case "updateCoins":
                    player.updateCoins();
                    break;
                case "updatePoints":
                    player.updatePoints();
                    break;
            }
        } else if (event.getTag().isEmpty()) {
            if (event.getSender() instanceof ProxiedPlayer) {
                OriginPlayer player = OriginManager.getInstance().getPlayer((ProxiedPlayer) event.getSender());
                player.disconnect(
                        player.language(
                                "§fYou've §cbeen §fkicked§f!\n" +
                                        "\n" +
                                        "§fReason§7: §cUnsupported modification" +
                                        "\n\n" +
                                        "§cTo continue playing on our network, " +
                                        "§cdeactivate the used modification.",
                                "§fDu §cwurdest §fgekickt§f!\n" +
                                        "\n" +
                                        "§fGrund§7: §cNicht unterstützte Modifikation" +
                                        "\n\n" +
                                        "§cUm weiterhin auf unserem Netzwerk spielen zu können, " +
                                        "§cdeaktiviere die benutzte Modifikation."
                        )
                );
            }
        }
    }
}
