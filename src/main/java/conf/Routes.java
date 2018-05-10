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


import controllers.*;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {  
        
        router.GET().route("/").with(ApplicationController::index);

        ///////////////////////////////////////////////////////////////////////
        // Interswitch
        ///////////////////////////////////////////////////////////////////////
        router.POST().route("/api/v1/payments/interswitch/paydirect").with(PayDirectController::doPayDirectRequest);
        router.GET().route("/interswitch").with(PrototypeController::interswitchPay);
        router.GET().route("/interswitch/assessment").with(PrototypeController::assRef);
        router.GET().route("/interswitch/poa").with(PrototypeController::poa);
        router.GET().route("/interswitch/dir").with(PrototypeController::dirCap);
        router.POST().route("/interswitch").with(PrototypeController::doMakePay);

        ///////////////////////////////////////////////////////////////////////
        // Quickteller
        ///////////////////////////////////////////////////////////////////////
        router.POST().route("/api/v1/payments/interswitch/quickteller").with(QuickTellerController::doQuickTellerNotification);
//        router.GET().route("/quickteller").with(PrototypeController::quickTeller);

        ///////////////////////////////////////////////////////////////////////
        // Merchant controller
        ///////////////////////////////////////////////////////////////////////
        router.POST().route("/api/v1/merchant").with(MerchantController::createMerchant);

        ///////////////////////////////////////////////////////////////////////
        // PaymentTransaction controller
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/api/v1/transactions").with(PaymentTransactionController::getPaymentTransactionDetails);
        router.GET().route("/api/v1/transactions/tickets/{transactionId}").with(PaymentTransactionController::getPaymentTransactionTicket);
        router.GET().route("/api/v1/transactions/{transactionId}/status").with(PaymentTransactionController::getPaymentTransactionStatus);

        router.POST().route("/api/v1/transactions").with(PaymentTransactionController::createPaymentTransaction);

        ///////////////////////////////////////////////////////////////////////
        // Notifications controller
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/api/v1/notify").with(NotificationController::sendNotifications);


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
