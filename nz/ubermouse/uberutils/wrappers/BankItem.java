package nz.ubermouse.uberutils.wrappers;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/8/11
 * Time: 7:05 PM
 * Package: nz.uberutils.wrappers;
 */
public class BankItem implements Serializable
{
    private final int    id;
    private final String name;
    private final int    quantity;

    public BankItem(int id, int quantity) {
        this(id, null, quantity);
    }

    public BankItem(String name, int quantity) {
        this(-1, name, quantity);
    }

    public BankItem(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "BankItem[id=" + id + "&name=" + name + "&quantity=" + quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
