/**
 * Copyright (C) 2012-2018 the original author or authors.
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


package conf;


import controllers.ApplicationController;
import controllers.CustomerValidationController;
import controllers.MerchantController;
import controllers.PaymentTransactionController;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {  
        
        router.GET().route("/").with(ApplicationController::index);
        router.GET().route("/hello_world.json").with(ApplicationController::helloWorldJson);

        ///////////////////////////////////////////////////////////////////////
        // Customer validation
        ///////////////////////////////////////////////////////////////////////
        router.POST().route("/api/v1/customer/validate").with(CustomerValidationController::validateCustomer);

        ///////////////////////////////////////////////////////////////////////
        // Merchant controller
        ///////////////////////////////////////////////////////////////////////
        router.POST().route("/api/v1/merchant").with(MerchantController::createMerchant);

        ///////////////////////////////////////////////////////////////////////
        // Merchant controller
        ///////////////////////////////////////////////////////////////////////
        router.POST().route("/api/v1/transaction").with(PaymentTransactionController::createPaymentTransaction);


        ///////////////////////////////////////////////////////////////////////
        // Assets (pictures / javascript)
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController::serveWebJars);
        router.GET().route("/assets/{fileName: .*}").with(AssetsController::serveStatic);

        ///////////////////////////////////////////////////////////////////////
        // Index / Catchall shows index page
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/.*").with(ApplicationController::index);
    }

}
