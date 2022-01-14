package me.boboballoon.innovativeitems.items.ability.trigger;

import java.lang.annotation.*;

/**
 * A simple annotation used to describe usage of the {@link AbilityTrigger} class without the bukkit event being registered automatically
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ManuallyRegister {}
