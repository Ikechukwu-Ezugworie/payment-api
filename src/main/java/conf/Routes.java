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
 */


package conf;


import com.google.inject.Inject;
import controllers.ApplicationController;
import controllers.NotificationController;
import controllers.PayDirectController;
import controllers.TestController;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;

public class Routes implements ApplicationRoutes {

    @Inject
    private NinjaProperties ninjaProperties;

    @Override
    public void init(Router router) {

        String urlPrefix = ninjaProperties.getWithDefault("url.prefix", "/");
        if (!urlPrefix.startsWith("/")) {
            urlPrefix = "/" + urlPrefix;
        }
        if (urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix.substring(0, urlPrefix.length() - 1);
        }


        ///////////////////////////////////////////////////////////////////////
        // Interswitch
        ///////////////////////////////////////////////////////////////////////
        router.POST().route(String.format("%s/api/v1/payments/interswitch/paydirect", urlPrefix)).with(PayDirectController::doPayDirectRequest);
        router.GET().route(String.format("%s/payments/interswitch/paydirect/monitor", urlPrefix)).with(PayDirectController::monitor);


        ///////////////////////////////////////////////////////////////////////
        // Assets (pictures / javascript)
        ///////////////////////////////////////////////////////////////////////
        router.GET().route(String.format("%s/assets/webjars/{fileName: .*}", urlPrefix)).with(AssetsController::serveWebJars);
        router.GET().route(String.format("%s/assets/{fileName: .*}", urlPrefix)).with(AssetsController::serveStatic);


        // TEST ROUTES
        if (!ninjaProperties.isProd()) {

            ///////////////////////////////////////////////////////////////////////
            // Notifications controller
            ///////////////////////////////////////////////////////////////////////
            router.GET().route(String.format("%s/api/v1/notify", urlPrefix)).with(NotificationController::sendNotifications);
            router.GET().route(String.format("%s/api/v1/backlog", urlPrefix)).with(NotificationController::processPaymentBacklog);

            ///////////////////////////////////////
            //////  TEST CONTROLLER
            ///////////////////////////////////////
            router.GET().route(String.format("%s/paydirect/test", urlPrefix)).with(TestController::test);
            router.POST().route(String.format("%s/api/paydirect/validate", urlPrefix)).with(TestController::doTest);
            router.POST().route(String.format("%s/api/paydirect/pay", urlPrefix)).with(TestController::doPay);


//            router.GET().route(String.format("%s/", urlPrefix)).with(ApplicationController::index);
//            router.GET().route(String.format("%s/interswitch", urlPrefix)).with(PrototypeController::interswitchPay);
//            router.GET().route(String.format("%s/interswitch/assessment", urlPrefix)).with(PrototypeController::assRef);
//            router.GET().route(String.format("%s/interswitch/poa", urlPrefix)).with(PrototypeController::poa);
//            router.GET().route(String.format("%s/interiswitch/dir", urlPrefix)).with(PrototypeController::dirCap);
//            router.POST().route(String.format("%s/interswitch", urlPrefix)).with(PrototypeController::doMakePay);
//
//            ///////////////////////////////////////////////////////////////////////
//            // Quick teller
//            ///////////////////////////////////////////////////////////////////////
//            router.POST().route(String.format("%s/api/v1/payments/interswitch/quickteller", urlPrefix)).with(QuickTellerController::doQuickTellerNotification);
//            router.GET().route(String.format("%s/api/v1/payments/interswitch/quickteller/update", urlPrefix)).with(QuickTellerController::updatePendingPayment);
//
//            ///////////////////////////////////////////////////////////////////////
//            // Merchant controller
//            ///////////////////////////////////////////////////////////////////////
////        router.POST().route(String.format("%s/api/v1/merchant",urlPrefix)).with(MerchantController::createMerchant);
//
//            ///////////////////////////////////////////////////////////////////////
//            // PaymentTransaction controller
//            ///////////////////////////////////////////////////////////////////////
//            router.GET().route(String.format("%s/api/v1/transactions", urlPrefix)).with(PaymentTransactionController::getPaymentTransactionDetails);
//            router.GET().route(String.format("%s/api/v1/transactions/{transactionId}/status", urlPrefix)).with(PaymentTransactionController::getPaymentTransactionStatus);
//            router.POST().route(String.format("%s/api/v1/transactions", urlPrefix)).with(PaymentTransactionController::createPaymentTransaction);
//            router.GET().route(String.format("%s/api/v1/transactions/tickets/{transactionId}", urlPrefix)).with(PaymentTransactionController::getPaymentTransactionTicket);
//
//            router.POST().route(String.format("%s/api/v1/transactions/ticket/new", urlPrefix)).with(PaymentTransactionController::createTicketForNewTransaction);
        }

        ///////////////////////////////////////////////////////////////////////
        // Index / Catchall shows index page
        ///////////////////////////////////////////////////////////////////////
        router.GET().route(String.format("%s/.*", urlPrefix)).with(ApplicationController::index);
    }

}
