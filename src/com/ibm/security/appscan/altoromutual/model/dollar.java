package com.ibm.security.appscan.altoromutual.model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Hashtable;


public class dollar {
    interface Expression{
        Money reduce(Bank bank, String to);
        Expression plus(Expression addend);
        Expression times(int multiplier);
    }

    static class Money implements Expression {
        protected int amount;

        public boolean equals(Object object) {
            Money money = (Money) object;
            return amount == money.amount
                    && currency().equals(money.currency());
        }

        static Money dollar(int amount) {
            return new Money(amount, "USD");
        }

        static Money franc(int amount) {
            return new Money(amount, "CHF");
        }

        Money(int amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }

        protected String currency;

        String currency() {
            return currency;
        }

        public String toString() {
            return amount + " " + currency;
        }

        public Money reduce(Bank bank, String to) {
            int rate = bank.rate(currency, to);
            return new Money(amount / rate, to);
        }

        public Expression times(int multiplier) {
            return new Money(amount * multiplier, currency);
        }

        public Expression plus(Expression addend) {
            return new Sum(this, addend);
        }

    }


    static Money dollar(int amount) {
        return new Money(amount, "USD");
    }

    static Money franc(int amount) {
        return new Money(amount, "CHF");
    }

    class Bank {

        Money reduce(Expression source, String to) {
            return source.reduce(this, to);
        }

        int rate(String from, String to) {
            if (from.equals(to)) return 1;
            Integer rate= (Integer) rates.get(new Pair(from, to));
            return rate.intValue();
        }

        private Hashtable rates= new Hashtable();

        void addRate(String from, String to, int rate) {
            rates.put(new Pair(from, to), new Integer(rate));
        }

    }

    static class Sum implements Expression{

        public Money reduce(Bank bank, String to) {
            int amount= augend.reduce(bank, to).amount
                    + addend.reduce(bank, to).amount;
            return new Money(amount, to);
        }

        Expression augend;
        Expression addend;

        Sum(Expression augend, Expression addend) {
            this.augend= augend;
            this.addend= addend;
        }

        public Expression plus(Expression addend) {
            return new Sum(this, addend);
        }

        public Expression times(int multiplier) {
            return new Sum(augend.times(multiplier),addend.times(multiplier));
        }


    }

    private class Pair {
        private String from;
        private String to;
        Pair(String from, String to) {
            this.from= from;
            this.to= to;
        }

        public boolean equals(Object object) {
            Pair pair= (Pair) object;
            return from.equals(pair.from) && to.equals(pair.to);
        }

        public int hashCode() {
            return 0;
        }
    }

}
