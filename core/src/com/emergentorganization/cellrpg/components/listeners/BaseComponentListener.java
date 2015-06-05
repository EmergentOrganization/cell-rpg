package com.emergentorganization.cellrpg.components.listeners;

import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;

/**
 * A listener that is attached to
 *
 * Created by OrelBitton on 05/06/2015.
 */
public interface BaseComponentListener {

    // TODO: added, removed events if needed in the future

    /**
     * Called if the validation is successful.
     *
     * @param comp the parent component
     * @param msg the component message
     */
    public void run(BaseComponent comp, BaseComponentMessage msg);

    /**
     * Validates the component message.
     *
     * @return if the listener should run for the current message
     */
    public boolean validate(BaseComponentMessage msg);

}
