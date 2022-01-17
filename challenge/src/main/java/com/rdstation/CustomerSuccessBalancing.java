package com.rdstation;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.ValidationException;

import com.rdstation.exceptions.PremiseValidationException;

public class CustomerSuccessBalancing {

	private final List<CustomerSuccess> customerSuccessList;
	private final List<Customer> customerList;
	private final List<Integer> customerSuccessAway;

	private static final int MAX_CUSTOMER_SUCCESS = 1000;
	private static final int MAX_CUSTOMER = 1000000;

	private static final int MAX_CUSTOMER_SUCCESS_ID = 1000;
	private static final int MAX_CUSTOMER_ID = 1000000;

	private static final int MAX_CUSTOMER_SUCCESS_SCORE = 10000;
	private static final int MAX_CUSTOMER_SCORE = 100000;

	public CustomerSuccessBalancing(List<CustomerSuccess> customerSuccessList, List<Customer> customerList,
			List<Integer> customerSuccessAway) {
		this.customerSuccessList = customerSuccessList;
		this.customerList = customerList;
		this.customerSuccessAway = customerSuccessAway;
	}

	/**
	 * Valida as premissas definidas no documento
	 *
	 * @return boolean valida
	 * @throws ValidationException
	 */
	private void validatePremises() throws PremiseValidationException {
		// Todos os CSs têm níveis diferentes
		int sizeDif = customerSuccessList.stream().map(cs -> cs.getScore()).distinct().collect(Collectors.toList())
				.size();
		if (sizeDif < customerSuccessList.size()) {
			throw new PremiseValidationException("CustomerSuccess with same score error");
		}

		// 0 < n < 1.000, sendo n o número de CSs
		if (customerSuccessList.isEmpty() || customerSuccessList.size() > MAX_CUSTOMER_SUCCESS)
			throw new PremiseValidationException("CustomerSuccess list exceeds the limit");

		// 0 < m < 1.000.000, sendo m o número de clientes
		if (customerList.isEmpty() || customerList.size() > MAX_CUSTOMER)
			throw new PremiseValidationException("Customer list exceeds the limit");

		// 0 < id do cs < 1.000
		if (customerSuccessList.stream().filter(
				customerSuccess -> customerSuccess.getId() < 1 || customerSuccess.getId() > MAX_CUSTOMER_SUCCESS_ID)
				.count() > 0)
			throw new PremiseValidationException("CustomerSuccess Id out of range");

		// 0 < id do cliente < 1.000.000
		if (customerList.stream().filter(customer -> customer.getId() < 1 || customer.getId() > MAX_CUSTOMER_ID)
				.count() > 0)
			throw new PremiseValidationException("Customer Id out of range");

		// 0 < nível do cs < 10.000
		if (customerSuccessList.stream().filter(customerSuccess -> customerSuccess.getScore() < 1
				|| customerSuccess.getScore() > MAX_CUSTOMER_SUCCESS_SCORE).count() > 0)
			throw new PremiseValidationException("CustomerSuccess score out of range");

		// 0 < tamanho do cliente < 100.000
		if (customerList.stream()
				.filter(customer -> customer.getScore() < 1 || customer.getScore() > MAX_CUSTOMER_SCORE).count() > 0)
			throw new PremiseValidationException("Customer score out of range");
	}

	public int run() throws PremiseValidationException {
		// Executa as validações das premissas definidas no documento
		validatePremises();

		// Obtem o score mínimo dos Customer para que seja possível filtrar os
		// CustomerSuccess capazes
		// de atender
		int minScore = customerList.stream().mapToInt(Customer::getScore).min().orElse(0);

		// Filtra os CustomerSuccess disponíveis com base no score mínimo e na lista
		// 'customerSuccessAway' e ordenar de acordo com a capacidade individual
		List<CustomerSuccess> customerSucAvailOrdList = customerSuccessList.stream()
				.filter(customerSuccess -> customerSuccess.getScore() >= minScore
						&& !customerSuccessAway.contains(customerSuccess.getId()))
				.sorted(Comparator.comparingInt(CustomerSuccess::getScore)).collect(Collectors.toList());

		// Se não sobrou nenhum CustomerSuccess disponível, retorna 0
		if (customerSucAvailOrdList.size() == 0)
			return 0;

		// Se sobrou apenas um CustomerSuccess, ele será o escolhido
		if (customerSucAvailOrdList.size() == 1)
			return customerSucAvailOrdList.get(0).getId();

		// Avalia quantos clientes cada CustomerSuccess poderá atender e preenche o
		// contador auxiliar 'capability'
		for (CustomerSuccess customerSuccess : customerSucAvailOrdList) {
			for (int indiceCustomer = 0; indiceCustomer < customerList.size();) {
				Customer customer = customerList.get(indiceCustomer);
				if (customer.getScore() <= customerSuccess.getScore()) {
					customerList.remove(customer);
					customerSuccess.increaseCapability();
				} else {
					indiceCustomer++;
				}
			}
		}

		// Calcula a competencia máxima para ver quais CustomerSuccess foram escolhidos
		int maxCapability = customerSucAvailOrdList.stream().mapToInt(CustomerSuccess::getCapability).max().orElse(-1);

		// Filtra os CustomerSuccess com base no valor máximo encontrado
		List<CustomerSuccess> elected = customerSucAvailOrdList.stream()
				.filter(customerSuccess -> customerSuccess.getCapability() == maxCapability)
				.collect(Collectors.toList());

		// Se existe mais de um CustomerSuccess ocorreu empate e retorna 0
		if (elected.size() > 1)
			return 0;
		else
			return elected.get(0).getId();
	}
}
