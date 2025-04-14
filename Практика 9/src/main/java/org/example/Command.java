package org.example;

import java.sql.Connection;

public abstract class Command {
    protected final Connection conn;

    public Command(Connection conn) {
        this.conn = conn;
    }

    public abstract String getName();
    public abstract void execute(String[] args);
    public abstract String getHelp();
}
