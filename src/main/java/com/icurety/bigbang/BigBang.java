package com.icurety.bigbang;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class BigBang extends JavaPlugin implements Listener {


    private int MAX_QUEUE_SIZE = 10000;
    private Queue<Explosion> nextExplosions = new LinkedList<Explosion>();
    private Lock lock = new ReentrantLock();

    private static boolean explosionsRunning = false;


    private void startExplosionThread() {
        explosionsRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(explosionsRunning) {
                    lock.lock();
                    if(nextExplosions.size() > 0)
                    {
                        createExplosion(nextExplosions.remove());
                        System.out.println("Exploding! " + nextExplosions.size() + " left");
                    }
                    lock.unlock();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onEnable() {
        startExplosionThread();
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        explosionsRunning = false;
        // Plugin shutdown logic
    }

    private void createExplosion(Explosion explosion) {
        Location location = explosion.getLocation();
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 32, explosion.isIgniteFire(), true);
            }
        });

    }

    private void queueExplosion(Location location) {
        try {
            if(lock.tryLock(5, TimeUnit.MILLISECONDS)) {
                if(nextExplosions.size() < MAX_QUEUE_SIZE)
                    nextExplosions.add(new Explosion(location));
                lock.unlock();
            }
        } catch (InterruptedException e) {
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        queueExplosion(location);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if(event.getCause() == BlockIgniteEvent.IgniteCause.EXPLOSION) {
            queueExplosion(event.getBlock().getLocation());
        }
    }
}
