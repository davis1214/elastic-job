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

package com.ami.ddframe.job.lite.integrate.std.script;

import com.ami.ddframe.job.api.script.ScriptJob;
import com.ami.ddframe.job.config.script.ScriptJobConfiguration;
import com.ami.ddframe.job.lite.config.LiteJobConfiguration;
import com.ami.ddframe.job.lite.integrate.AbstractBaseStdJobAutoInitTest;
import com.ami.ddframe.job.lite.integrate.WaitingUtils;
import com.ami.ddframe.job.lite.internal.config.LiteJobConfigurationGsonFactory;
import com.ami.ddframe.job.lite.fixture.util.ScriptElasticJobUtil;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class ScriptElasticJobTest extends AbstractBaseStdJobAutoInitTest {
    
    public ScriptElasticJobTest() {
        super(ScriptJob.class);
    }
    
    @Test
    public void assertJobInit() throws IOException {
        ScriptElasticJobUtil.buildScriptCommandLine();
        WaitingUtils.waitingShortTime();
        String scriptCommandLine = ((ScriptJobConfiguration) getLiteJobConfig().getTypeConfig()).getScriptCommandLine();
        LiteJobConfiguration liteJobConfig = LiteJobConfigurationGsonFactory.fromJson(getRegCenter().get("/" + getJobName() + "/config"));
        assertThat(((ScriptJobConfiguration) liteJobConfig.getTypeConfig()).getScriptCommandLine(), is(scriptCommandLine));
    }
}
