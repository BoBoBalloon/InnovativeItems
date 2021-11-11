package me.boboballoon.innovativeitems.config;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a custom item from the config file that has not been finalized
 * @param <T> the type of custom item this class will create
 */
public class IncompleteItem<T extends CustomItem> {
    private final Class<T> clazz;
    private final Object[] parameters;
    private boolean hasAnonymousAbility;
    private T item;
    private boolean dirty;

    public IncompleteItem(@NotNull Class<T> clazz, @NotNull Object... parameters) {
        this.clazz = clazz;
        this.parameters = parameters;
        this.hasAnonymousAbility = this.parameters[1] instanceof Ability;
        this.item = null;
        this.dirty = false;
    }

    /**
     * A method used to return the type of custom item this class will create
     *
     * @return the type of custom item this class will create
     */
    public Class<T> getClazz() {
        return this.clazz;
    }

    /**
     * A method used to return the parameters of the class this object is trying to build
     *
     * @return the parameters of the class this object is trying to build
     */
    public Object[] getParameters() {
        return this.parameters;
    }

    /**
     * A method used to return a boolean that is true when this item has an anonymous ability
     *
     * @return a boolean that is true when this item has an anonymous ability
     */
    public boolean hasAnonymousAbility() {
        return this.hasAnonymousAbility;
    }

    /**
     * A method used to return a boolean that is true when this object has been modified the the output of the item was not updated
     *
     * @return a boolean that is true when this object has been modified the the output of the item was not updated
     */
    public boolean isDirty() {
        return this.dirty;
    }

    /**
     * A method used to set the ability this incomplete item is carrying
     *
     * @param ability the ability this incomplete item is carrying
     */
    public void setAbility(@NotNull Ability ability) {
        this.parameters[1] = ability;
        this.hasAnonymousAbility = true;
        this.dirty = true;
    }

    /**
     * A method used to set the ability this incomplete item is carrying
     *
     * @param abilityName the name of the ability this incomplete item is carrying
     */
    public void setAbility(@NotNull String abilityName) {
        this.parameters[1] = abilityName;
        this.hasAnonymousAbility = false;
        this.dirty = true;
    }

    /**
     * A method used to set the ability this incomplete item is carrying
     */
    public void setAbilityAsNull() {
        this.parameters[1] = null;
        this.hasAnonymousAbility = false;
        this.dirty = true;
    }

    /**
     * A method used to return the instance of this incomplete item as a completed custom item
     *
     * @return a completed custom item
     * @throws Exception when reflection failed in some way or the ability was not registered in time for some reason
     */
    @NotNull
    public T build() throws Exception {
        if (this.item != null && !this.dirty) {
            return this.item;
        }

        //if this has no anonymous ability, grab ability from cache
        if (!this.hasAnonymousAbility && this.parameters[1] instanceof String) {
            this.parameters[1] = InnovativeItems.getInstance().getItemCache().getAbility((String) this.parameters[1]);
        }

        List<Class<?>> parameterTypes = new ArrayList<>();
        for (int i = 0; i < this.parameters.length; i++) {
            if (i == 1) {
                parameterTypes.add(Ability.class);
                continue;
            }

            parameterTypes.add(this.parameters[i].getClass());
        }

        Constructor<T> constructor = this.clazz.getConstructor(parameterTypes.toArray(new Class[0]));

        this.item = constructor.newInstance(this.parameters);
        this.dirty = false;

        return this.item;
    }
}
