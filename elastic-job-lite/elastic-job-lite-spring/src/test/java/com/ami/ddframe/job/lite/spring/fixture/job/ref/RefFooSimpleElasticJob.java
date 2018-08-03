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

package com.ami.ddframe.job.lite.spring.fixture.job.ref;

import com.ami.ddframe.job.api.ShardingContext;
import com.ami.ddframe.job.api.simple.SimpleJob;
import com.ami.ddframe.job.lite.spring.fixture.service.FooService;
import lombok.Getter;
import lombok.Setter;

public class RefFooSimpleElasticJob implements SimpleJob {

    @Getter
    private static volatile boolean completed;
    
    @Getter
    @Setter
    private FooService fooService;
    
    @Override
    public void execute(final ShardingContext shardingContext) {
        fooService.foo();
        completed = true;
    }

    public static void reset() {
        completed = false;
    }
}