package de.claved.origin.spigot.api.manager;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.inventory.InventoryBuilder;
import de.claved.origin.spigot.api.inventory.ItemStackBuilder;
import de.claved.origin.utils.enums.Rank;
import de.claved.origin.utils.objects.origin.OriginReport;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReportManager implements Listener {

    @Getter
    private static ReportManager instance;

    private final QueryStatement getReports;
    private final QueryStatement getAllReports;

    private final UpdateStatement closeReport;

    public ReportManager() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, Origin.getInstance());
        Session session = Origin.getInstance().getSession();

        getReports = session.prepareQueryStatement("SELECT * FROM reports WHERE target = ? AND state = ?");
        getAllReports = session.prepareQueryStatement("SELECT * FROM reports WHERE state = ?");

        closeReport = session.prepareUpdateStatement("UPDATE reports SET state = ? WHERE target = ? AND id = ?");

        Bukkit.getScheduler().runTaskTimerAsynchronously(Origin.getInstance(), () -> {
            if (!OriginManager.getInstance().getPlayers().isEmpty() && !getReports().isEmpty()) {
                OriginManager.getInstance().getPlayers().forEach(players -> {
                    if (players.hasPriorityAccess(Rank.SUPPORTER.getPriority()) && NotifyManager.getInstance().getObject(players.getUniqueId()).get("report").getAsBoolean()) {
                        players.sendActionbar("§8» §7" + players.language("Aktuelle Reports", "Current reports") + "§8: §c" + getReports().size() + " §8«");
                    }
                });
            }
        }, 0, 20);
    }

    public void disable() {
        instance = this;
    }

    public List<OriginReport> getReports(UUID uuid, OriginReport.ReportState state) {
        List<OriginReport> reports = new ArrayList<>();
        try {
            ResultSet resultSet = getReports.execute(uuid, state.name());
            while (resultSet.next()) {
                reports.add(new OriginReport(
                        resultSet.getString("id"),
                        OriginReport.ReportType.valueOf(resultSet.getString("reason")),
                        UUID.fromString(resultSet.getString("target")),
                        UUID.fromString(resultSet.getString("executor")),
                        resultSet.getString("server"),
                        Timestamp.valueOf(resultSet.getString("date")),
                        OriginReport.ReportState.valueOf(resultSet.getString("state"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public List<OriginReport> getReports() {
        List<OriginReport> reports = new ArrayList<>();
        try {
            ResultSet resultSet = getAllReports.execute(OriginReport.ReportState.OPENED.name());
            while (resultSet.next()) {
                reports.add(new OriginReport(
                        resultSet.getString("id"),
                        OriginReport.ReportType.valueOf(resultSet.getString("reason")),
                        UUID.fromString(resultSet.getString("target")),
                        UUID.fromString(resultSet.getString("executor")),
                        resultSet.getString("server"),
                        Timestamp.valueOf(resultSet.getString("date")),
                        OriginReport.ReportState.valueOf(resultSet.getString("state"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    private void closeReport(OriginReport report, OriginReport.ReportState state) {
        try {
            closeReport.execute(state.name(), report.getTarget(), report.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void openGUI(OriginPlayer player) {
        InventoryBuilder inventory = new InventoryBuilder(6, "§8Reports");

        IntStream.range(1, 10).forEach(slot -> inventory.setItem(1, slot, new ItemStackBuilder(Material.STAINED_GLASS_PANE).setData((short) 7).setNoName().build()));
        IntStream.range(1, 10).forEach(slot -> inventory.setItem(6, slot, new ItemStackBuilder(Material.STAINED_GLASS_PANE).setData((short) 7).setNoName().build()));

        Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), () -> {
            List<OriginReport> reports = getReports().stream().filter(report -> report.getState() == OriginReport.ReportState.OPENED).collect(Collectors.toList());
            if (reports.size() != 0) {
                reports.forEach(report -> inventory.addItem(new ItemStackBuilder(Material.SKULL_ITEM).setData((short) 3).setSkullOwner(OriginManager.getInstance().getName(report.getTarget())).setDisplayName("§c" + OriginManager.getInstance().getName(report.getTarget()))
                        .addLoreLine("§8§m------------------")
                        .addLoreLine("§fID§8: §e" + report.getId())
                        .addLoreLine("§f" + player.language("Grund", "Reason") + "§8: §e" + report.getType().name())
                        .addLoreLine("§fReporter§8: §e" + OriginManager.getInstance().getName(report.getExecutor()))
                        .addLoreLine("§fServer§8: §e" + report.getServer())
                        .addLoreLine("§f" + player.language("Datum", "Date") + "§8: §e" + report.getFormattedDate())
                        .addLoreLine("§8§m------------------")
                        .addLoreLine("§f" + player.language("Linksklick", "Left click") + "§8: §a" + player.language("Annehmen", "Accept"))
                        .addLoreLine("§f" + player.language("Rechtsklick", "Right click") + "§8: §c" + player.language("Ablehnen", "Close"))
                        .addLoreLine("§f" + player.language("Mittelklick", "Middle click") + "§8: §b" + player.language("Beobachten", "Spectate"))
                        .build()));
            } else {
                inventory.setItem(3, 5, new ItemStackBuilder(Material.BARRIER).setDisplayName(player.language("§cDerzeit gibt es keine offenen Reports!", "§cCurrently there are no reports open!")).build());
            }
        });

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CLICK, 10F, 10F);
    }

    public void openUserGui(OriginPlayer player) {
        InventoryBuilder reportinventory = new InventoryBuilder(6, "§8Report");



        player.openInventory(reportinventory);
        player.playSound(player.getLocation(), Sound.CLICK, 10F, 10F);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer((Player) event.getWhoClicked());

        if (event.getInventory() == null || event.getView() == null || event.getCurrentItem() == null) return;

        if (event.getView().getTitle().equals("§8Reports")) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType().equals(Material.SKULL_ITEM)) {
                String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

                Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), () -> {
                    OriginReport originReport = getReports().stream().filter(report -> report.getState() == OriginReport.ReportState.OPENED).filter(report -> Objects.equals(name, OriginManager.getInstance().getName(report.getTarget()))).findFirst().orElse(null);

                    if (originReport != null) {
                        switch (event.getClick()) {
                            case LEFT:
                                player.sendMessage(
                                        Origin.getInstance().getPrefix() + "§7Du hast den Report §aangenommen",
                                        Origin.getInstance().getPrefix() + "§7You have §accepted §7the report"
                                );
                                player.playSound(player.getLocation(), Sound.LEVEL_UP, 10F, 10F);
                                player.closeInventory();
                                closeReport(originReport, OriginReport.ReportState.ACCEPTED);
                                break;
                            case RIGHT:
                                player.sendMessage(
                                        Origin.getInstance().getPrefix() + "§7Du hast den Report §cgeschlossen",
                                        Origin.getInstance().getPrefix() + "§7You have §cclosed §7the report"
                                );
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10F, 10F);
                                player.closeInventory();
                                closeReport(originReport, OriginReport.ReportState.CLOSED);
                                break;
                            case MIDDLE:
                                player.sendMessage(
                                        Origin.getInstance().getPrefix() + "§7Du wirst mit §e" + originReport.getServer() + " §7verbunden",
                                        Origin.getInstance().getPrefix() + "§7You will be connected with §e" + originReport.getServer()
                                );
                                player.playSound(player.getLocation(), Sound.LEVEL_UP, 10F, 10F);
                                player.closeInventory();
                                player.send(originReport.getServer());
                                break;
                        }
                    } else {
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10F, 10F);
                    }
                });
            }
        }
    }
}
