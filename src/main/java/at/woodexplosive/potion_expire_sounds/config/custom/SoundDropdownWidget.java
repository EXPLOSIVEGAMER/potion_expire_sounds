package at.woodexplosive.potion_expire_sounds.config.custom;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement;
import dev.isxander.yacl3.gui.controllers.dropdown.DropdownWidget;

public class SoundDropdownWidget extends DropdownWidget<String> {

    public SoundDropdownWidget(AbstractDropdownController<String> control, YACLScreen screen, Dimension<Integer> dim, AbstractDropdownControllerElement<String, ?> dropdownElement) {
        super(control, screen, dim, dropdownElement);
    }
}
