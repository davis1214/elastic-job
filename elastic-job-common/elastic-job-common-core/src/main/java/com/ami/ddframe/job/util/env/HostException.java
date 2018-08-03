/*
 * Copyright 1999-2015 ami.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.ami.ddframe.job.util.env;

import java.io.IOException;

/**
 * 网络主机异常.
 * 
 * @author zhangliang
 */
public final class HostException extends RuntimeException {
    
    private static final long serialVersionUID = 3589264847881174997L;
    
    public HostException(final IOException cause) {
        super(cause);
    }
}
