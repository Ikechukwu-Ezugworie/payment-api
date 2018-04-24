/**
 * Copyright (C) 2012-2018 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Copyright (C) 2013 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Copyright (C) 2013 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Copyright (C) 2013 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Copyright (C) 2013 the original author or authors.
 *
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
 */

package controllers;

import com.bw.payment.entity.Merchant;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.MerchantDao;
import ninja.Context;
import ninja.Result;
import ninja.i18n.Messages;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.MerchantRequestPojo;
import utils.ResponseUtil;
import utils.ValidationUtils;


@Singleton
public class MerchantController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private Messages messages;
    @Inject
    private MerchantDao merchantDao;

    public Result createMerchant(@JSR303Validation MerchantRequestPojo request, Validation validation, Context context) {
        if (validation.hasViolations()) {
            return ResponseUtil.returnJsonResult(Result.SC_400_BAD_REQUEST,
                    ValidationUtils.getFirstViolationMessage(context, messages, validation));
        }

        Merchant merchant = null;
        try {
            merchant = merchantDao.createMerchant(request);
        } catch (IllegalArgumentException ile) {
            ile.printStackTrace();
            return ResponseUtil.returnJsonResult(400, ile.getMessage());
        }
        merchant.setDateCreated(null);
        merchant.setLastModified(null);
        merchant.setId(null);

        return ResponseUtil.returnJsonResult(201, null, merchant);
    }
}
