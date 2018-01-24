/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.llv.mcauth;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author toyblocks
 */
public class MCAuth extends JavaPlugin {

    private static final char[] CHARS_DISTINGUISHABLE = {
        '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'k', 'm', 'n', 'p',
        'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P',
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',};
    private static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "pJaupi" + ChatColor.GRAY + "] " + ChatColor.RESET;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
    }

    @Override
    public void onDisable() {
        deleteDir(getDataFolder());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mcauth") || !(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Insufficient Permission!");
            return true;
        }
        UUID uuid = ((Player) sender).getUniqueId();
        String code = generateRandomString(8);
        File file = new File(getDataFolder(), uuid.toString());
        try (Writer writer = new OutputStreamWriter(
                new BufferedOutputStream(
                        new FileOutputStream(file, false)
                )
        )) {
            writer.write(code);
        } catch (IOException ex) {
            getLogger().log(Level.WARNING, "Auth code cannot be exported", ex);
            sender.sendMessage(PREFIX + ChatColor.RED + "Sorry, something went wrong");
            return true;
        }
        sender.sendMessage(PREFIX + "Your auth code is `" + ChatColor.GREEN + code + ChatColor.RESET + "`");
        return true;
    }

    public static String generateRandomString(int length) {
        char[] chars = new char[length];
        Random random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            chars[i] = CHARS_DISTINGUISHABLE[random.nextInt(CHARS_DISTINGUISHABLE.length)];
        }
        return String.valueOf(chars);
    }

    public static boolean deleteDir(File file) {
        if (!file.isFile()) {
            for (File child : file.listFiles()) {
                if (!deleteDir(child)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

}
