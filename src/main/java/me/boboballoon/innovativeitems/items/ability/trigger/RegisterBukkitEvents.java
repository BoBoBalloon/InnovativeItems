package me.boboballoon.innovativeitems.items.ability.trigger;

import java.lang.annotation.*;

/**
 * A simple annotation used to describe usage of the {@link AbilityTrigger} class when the function manager should register other non custom item related events as well
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisterBukkitEvents {}
