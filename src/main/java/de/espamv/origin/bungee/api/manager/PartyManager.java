package de.claved.origin.bungee.api.manager;

import de.claved.origin.bungee.Origin;
import de.claved.origin.bungee.api.OriginPlayer;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Getter
public class PartyManager implements Listener {

    @Getter
    private static PartyManager instance;

    @Getter
    private final String prefix = "§8[ §5Party §8] §r";

    private final HashSet<Party> parties = new HashSet<>();

    public PartyManager() {
        instance = this;
        ProxyServer.getInstance().getPluginManager().registerListener(Origin.getInstance(), this);
    }

    public void disable() {
        instance = null;
    }

    public Party getParty(OriginPlayer player) {
        return parties.stream().filter(party -> party.getLeader() == player || party.getMembers().contains(player)).findFirst().orElse(null);
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());
        Party party = getParty(player);

        if (party != null) {
            if (party.getLeader() == player) {
                party.getMembers().forEach(players -> {
                    players.connect(player.getServer());
                    players.sendMessage(
                            prefix + "§7Deine Party betritt den Server §e" + player.getServer().getName(),
                            prefix + "§7Your party is entering the server §e" + player.getServer().getName()
                    );
                });
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());
        Party party = getParty(player);

        if (party != null) {
            if (party.getMembers().contains(player)) {
                party.leave(player);
            } else if (party.getLeader() == player) {
                if (party.getMembers().size() != 0) {
                    party.setRandomLeader();
                } else {
                    parties.remove(party);
                }
            }
        }
    }

    @Getter
    public static class Party {

        @Setter
        private OriginPlayer leader;

        private final HashSet<OriginPlayer> members = new HashSet<>();
        private final HashSet<OriginPlayer> invitations = new HashSet<>();

        public Party(OriginPlayer leader) {
            this.leader = leader;
            PartyManager.getInstance().getParties().add(this);
        }

        public void setRandomLeader() {
            OriginPlayer player = members.stream().findAny().orElse(null);
            if (player != null) {
                OriginPlayer primaryLeader = leader;
                leader = player;

                leave(primaryLeader);

                leader.sendMessage(
                        instance.getPrefix() + "§7Du bist nun der §eLeiter §7der Party",
                        instance.getPrefix() + "§7Your are now the §eleader §7of the party"
                );
                members.forEach(players -> players.sendMessage(
                        instance.getPrefix() + leader.getDisplayName() + " §7ist nun der §eLeiter §7der Party",
                        instance.getPrefix() + leader.getDisplayName() + " §7is now the §eleader §7of the party"
                ));
            }
        }

        public void invite(OriginPlayer player) {
            invitations.add(player);

            TextComponent accept = new TextComponent(instance.getPrefix() + "§7" + player.language("Annehmen", "Accept") + "§8: §a/party accept " + leader.getName());
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + leader.getName()));

            TextComponent deny = new TextComponent(instance.getPrefix() + "§7" + player.language("Ablehnen", "Deny") + "§8: §c/party deny " + leader.getName());
            deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + leader.getName()));

            leader.sendMessage(
                    instance.getPrefix() + player.getDisplayName() + " §7wurde zu deiner Party §aeingeladen",
                    instance.getPrefix() + player.getDisplayName() + " §7has been §ainvited §7into your party"
            );
            player.sendMessage(
                    instance.getPrefix() + "§7Du wurdest zur Party von " + leader.getDisplayName() + " §7eingeladen",
                    instance.getPrefix() + "§7You've been invited into the party of " + leader.getDisplayName()
            );
            player.sendMessage(accept);
            player.sendMessage(deny);

            ProxyServer.getInstance().getScheduler().schedule(Origin.getInstance(), () -> {
                if (invitations.contains(player)) {
                    invitations.remove(player);
                    player.sendMessage(
                            instance.getPrefix() + "§7Die Einladung zur Party von " + leader.getDisplayName() + " §7ist §cabgelaufen",
                            instance.getPrefix() + "§7The invitation to the party of " + leader.getDisplayName() + " §7has §cexpired"
                    );
                }
            }, 1, TimeUnit.MINUTES);
        }

        public void acceptInvite(OriginPlayer player) {
            invitations.remove(player);
            members.add(player);

            leader.sendMessage(
                    instance.getPrefix() + player.getDisplayName() + " §7hat die Party Einladung §aakzeptiert",
                    instance.getPrefix() + player.getDisplayName() + " §7has §aaccepted §7your party invitation"
            );
            members.forEach(players -> players.sendMessage(
                    instance.getPrefix() + player.getDisplayName() + " §7ist der Party §abeigetreten",
                    instance.getPrefix() + player.getDisplayName() + " §7has §ajoined §7the party"
            ));
        }

        public void denyInvite(OriginPlayer player) {
            invitations.remove(player);

            player.sendMessage(
                    instance.getPrefix() + "§7Du hast die Party Einladung §cabgelehnt",
                    instance.getPrefix() + "§7You have §cdenied §7your party invitation"
            );
            leader.sendMessage(
                    instance.getPrefix() + player.getDisplayName() + " §7hat die Party Einladung §cabgelehnt",
                    instance.getPrefix() + player.getDisplayName() + " §7has §cdenied §7your party invitation"
            );
        }

        public void leave(OriginPlayer player) {
            leader.sendMessage(
                    instance.getPrefix() + player.getDisplayName() + " §7hat die Party §cverlassen",
                    instance.getPrefix() + player.getDisplayName() + " §7has §cleft §7the party"
            );
            members.forEach(players -> players.sendMessage(
                    instance.getPrefix() + player.getDisplayName() + " §7hat die Party §cverlassen",
                    instance.getPrefix() + player.getDisplayName() + " §7has §cleft §7the party"
            ));

            if (members.contains(player)) {
                members.remove(player);
            } else if (leader == player) {
                if (members.size() != 0) {
                    setRandomLeader();
                } else {
                    instance.getParties().remove(this);
                }
            }
        }

        public void promote(OriginPlayer player) {
            leader = player;

            leader.sendMessage(
                    instance.getPrefix() + "§7Du bist nun der §eLeiter §7der Party",
                    instance.getPrefix() + "§7Your are now the §eleader §7of the party"
            );
            members.forEach(players -> players.sendMessage(
                    instance.getPrefix() + leader.getDisplayName() + " §7ist nun der §eLeiter §7der Party",
                    instance.getPrefix() + leader.getDisplayName() + " §7is now the §eleader §7of the party"
            ));
        }
    }
}
