package com.rdstation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import com.rdstation.exceptions.PremiseValidationException;

public class CustomerSuccessBalancingTest {

	@Test
	public void scenario1() throws PremiseValidationException {
		List<CustomerSuccess> css = toList(new CustomerSuccess(1, 60), new CustomerSuccess(2, 20),
				new CustomerSuccess(3, 95), new CustomerSuccess(4, 75));
		List<Customer> customers = toList(new Customer(1, 90), new Customer(2, 20), new Customer(3, 70),
				new Customer(4, 40), new Customer(5, 60), new Customer(6, 10));

		List<Integer> csAway = toList(2, 4);

		assertEquals(new CustomerSuccessBalancing(css, customers, csAway).run(), 1);
	}

	@Test
	public void scenario2() throws PremiseValidationException {
		List<CustomerSuccess> css = mapCustomerSuccess(11, 21, 31, 3, 4, 5);
		List<Customer> customers = mapCustomers(10, 10, 10, 20, 20, 30, 30, 30, 20, 60);
		List<Integer> csAway = Collections.emptyList();

		assertEquals(new CustomerSuccessBalancing(css, customers, csAway).run(), 0);
	}

	@Test(timeout = 100)
	public void scenario3() throws PremiseValidationException {
		List<CustomerSuccess> css = mapCustomerSuccess(IntStream.range(1, 999).toArray());
		List<Customer> customers = buildSizeEntities(100000, 998);
		List<Integer> csAway = toList(999);

		assertEquals(new CustomerSuccessBalancing(css, customers, csAway).run(), 998);
	}

	@Test
	public void scenario4() throws PremiseValidationException {
		List<CustomerSuccess> css = mapCustomerSuccess(1, 2, 3, 4, 5, 6);
		List<Customer> customers = mapCustomers(10, 10, 10, 20, 20, 30, 30, 30, 20, 60);
		List<Integer> csAway = Collections.emptyList();

		assertEquals(new CustomerSuccessBalancing(css, customers, csAway).run(), 0);
	}

	@Test
	public void scenario5() throws PremiseValidationException {
		// Precisei alterar a sequencia passada para atender a premissa de CS's
		// diferentes
		List<CustomerSuccess> css = mapCustomerSuccess(100, 1, 2, 3, 4, 5);
		List<Customer> customers = mapCustomers(10, 10, 10, 20, 20, 30, 30, 30, 20, 60);
		List<Integer> csAway = Collections.emptyList();

		assertEquals(new CustomerSuccessBalancing(css, customers, csAway).run(), 1);
	}

	@Test
	public void scenario6() throws PremiseValidationException {
		List<CustomerSuccess> css = mapCustomerSuccess(100, 99, 88, 3, 4, 5);
		List<Customer> customers = mapCustomers(10, 10, 10, 20, 20, 30, 30, 30, 20, 60);
		List<Integer> csAway = toList(1, 3, 2);

		assertEquals(new CustomerSuccessBalancing(css, customers, csAway).run(), 0);
	}

	@Test
	public void scenario7() throws PremiseValidationException {
		List<CustomerSuccess> css = mapCustomerSuccess(100, 99, 88, 3, 4, 5);
		List<Customer> customers = mapCustomers(10, 10, 10, 20, 20, 30, 30, 30, 20, 60);
		List<Integer> csAway = toList(4, 5, 6);

		assertEquals(new CustomerSuccessBalancing(css, customers, csAway).run(), 3);
	}

	// A PARTIR DAQUI SÃO TESTES NOVOS CRIADOS PARA VALIDAS AS PREMISSAS

	@Test
	public void testCSNiveisIguais() {
		List<CustomerSuccess> css = mapCustomerSuccess(10, 10, 10, 30, 30, 55);
		List<Customer> customers = mapCustomers(10, 10, 10, 20, 20, 30, 30, 30, 20, 60);
		List<Integer> csAway = Collections.emptyList();

		try {
			new CustomerSuccessBalancing(css, customers, csAway).run();
			fail("Exception not throw");
		} catch (PremiseValidationException e) {
			assertEquals(e.getMessage(), "CustomerSuccess with same score error");
		}
	}

	@Test
	public void testOutOfLimitCustomSuccess() {
		List<CustomerSuccess> css = mapCustomerSuccess(IntStream.range(1, 1005).toArray());
		List<Customer> customers = buildSizeEntities(10, 50);
		List<Integer> csAway = Collections.emptyList();

		try {
			new CustomerSuccessBalancing(css, customers, csAway).run();
			fail("Exception not throw");
		} catch (PremiseValidationException e) {
			assertEquals(e.getMessage(), "CustomerSuccess list exceeds the limit");
		}
	}

	@Test
	public void testOutOfLimitCustoms() {
		List<CustomerSuccess> css = mapCustomerSuccess(IntStream.range(1, 10).toArray());
		List<Customer> customers = buildSizeEntities(1000001, 998);
		List<Integer> csAway = Collections.emptyList();

		try {
			new CustomerSuccessBalancing(css, customers, csAway).run();
			fail("Exception not throw");
		} catch (PremiseValidationException e) {
			assertEquals(e.getMessage(), "Customer list exceeds the limit");
		}
	}

	@Test
	public void testIdOutOfRangeCustomerSuccess() {
		List<CustomerSuccess> css = toList(new CustomerSuccess(100000, 60), new CustomerSuccess(4, 75));
		List<Customer> customers = toList(new Customer(1, 90), new Customer(10, 20), new Customer(6, 10));

		List<Integer> csAway = Collections.emptyList();

		try {
			new CustomerSuccessBalancing(css, customers, csAway).run();
			fail("Exception not throw");
		} catch (PremiseValidationException e) {
			assertEquals(e.getMessage(), "CustomerSuccess Id out of range");
		}
	}

	@Test
	public void testIdOutOfRangeCustomer() {
		List<CustomerSuccess> css = toList(new CustomerSuccess(1, 60), new CustomerSuccess(2, 20));
		List<Customer> customers = toList(new Customer(1000001, 90), new Customer(6, 10));

		List<Integer> csAway = Collections.emptyList();

		try {
			new CustomerSuccessBalancing(css, customers, csAway).run();
			fail("Exception not throw");
		} catch (PremiseValidationException e) {
			assertEquals(e.getMessage(), "Customer Id out of range");
		}
	}

	@Test
	public void testScoreOutOfRangeCustomerSuccess() {
		List<CustomerSuccess> css = toList(new CustomerSuccess(1, 10001), new CustomerSuccess(4, 75));
		List<Customer> customers = toList(new Customer(1, 90), new Customer(10, 20), new Customer(6, 10));

		List<Integer> csAway = Collections.emptyList();

		try {
			new CustomerSuccessBalancing(css, customers, csAway).run();
			fail("Exception not throw");
		} catch (PremiseValidationException e) {
			assertEquals(e.getMessage(), "CustomerSuccess score out of range");
		}
	}

	@Test
	public void testScoreOutOfRangeCustomer() {
		List<CustomerSuccess> css = toList(new CustomerSuccess(1, 60), new CustomerSuccess(2, 20));
		List<Customer> customers = toList(new Customer(3, 100001), new Customer(6, 10));

		List<Integer> csAway = Collections.emptyList();

		try {
			new CustomerSuccessBalancing(css, customers, csAway).run();
			fail("Exception not throw");
		} catch (PremiseValidationException e) {
			assertEquals(e.getMessage(), "Customer score out of range");
		}
	}

	private List<CustomerSuccess> mapCustomerSuccess(int... scores) {
		List<CustomerSuccess> entities = new ArrayList<>(scores.length);
		for (int i = 0; i < scores.length; i++) {
			entities.add(new CustomerSuccess(i + 1, scores[i]));
		}
		return entities;
	}

	private List<Customer> mapCustomers(int... scores) {
		List<Customer> entities = new ArrayList<>(scores.length);
		for (int i = 0; i < scores.length; i++) {
			entities.add(new Customer(i + 1, scores[i]));
		}
		return entities;
	}

	private List<Customer> buildSizeEntities(int size, int score) {
		List<Customer> entities = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			entities.add(new Customer(i + 1, score));
		}
		return entities;
	}

	private <T> List<T> toList(T... values) {
		return Arrays.stream(values).collect(Collectors.toList());
	}
}
