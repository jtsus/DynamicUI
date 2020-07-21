package net.eterniamc.dynamicui;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

/**
 * Copyright © 2020 Property of HQGAMING STUDIO, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

@RequiredArgsConstructor
@Getter
public abstract class PromptUI<T> extends DynamicUI {

    private final DynamicUI parent;

    private Consumer<T> callback = t -> {};

    @Override
    public boolean onClose() {
        parent.open(player, true);

        return super.onClose();
    }

    public PromptUI<T> setCallback(Consumer<T> function) {
        this.callback = function;
        return this;
    }
}

