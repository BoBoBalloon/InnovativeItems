package me.boboballoon.innovativeitems.functions.arguments;

import java.lang.annotation.*;

/**
 * An annotation used to mark an argument that can only be loaded after all abilities are complete
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresAbilityCompletion {}