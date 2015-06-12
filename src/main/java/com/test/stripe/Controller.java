package com.test.stripe;

import com.stripe.exception.*;
import org.springframework.util.StringUtils;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: hbhatia
 * Date: 6/11/15
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Named
@Path("/")
public class Controller {

    PaymentService paymentService = new PaymentService();

    @POST
    @Path("/makepaymentandsaveuser")
    public Response doPaymentAndSaveCustomer(@FormParam("stripeToken")String stripeToken, @FormParam("stripeEmail")String stripeEmail,
                     @FormParam("amount") String amount, @FormParam("saveuser") String saveuser, @FormParam("stripeTokenType") String stripeTokenType)
            throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        System.out.println("input = " + stripeToken );
        if(!StringUtils.isEmpty(stripeTokenType))
            saveuser = "saveuser";
        paymentService.doPaymentAndSaveCustomer(stripeToken, amount, stripeEmail, saveuser);
        return Response.ok("<h3>Payment Successful</h3>").build();
    }

    @POST
    @Path("/makepaymentofcustomer")
    public Response doPaymentOfCustomer(@FormParam("email")String stripeEmail, @FormParam("amount") String amount)
            throws Exception {
        String status = paymentService.doPaymentByCustomer(amount, stripeEmail);
        return Response.ok("<h3>" +status+ "</h3>").build();
    }

    @GET
    @Path("/info")
    public Response getinfo() {
        String result = null;
        System.out.println(result);
        return Response.ok(result).build();
    }
}
