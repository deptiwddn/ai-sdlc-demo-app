import { test, expect } from '@playwright/test';

test('frontend approval flow', async ({ page }) => {
  await page.goto('http://localhost:8080/');

  await page.getByRole('textbox', { name: 'Username' }).fill('demo.director');
  await page.getByRole('textbox', { name: 'Password' }).fill('ChangeMe123!');
  await page.getByRole('button', { name: 'Sign in' }).click();

  await page.getByRole('textbox', { name: 'Customer name' }).fill('Test2 ');
  await page.getByRole('spinbutton', { name: 'Amount' }).fill('1000');
  await page.getByRole('button', { name: 'Create invoice' }).click();

  await page.getByRole('tab', { name: 'Approvals' }).click();
  await page.getByRole('button', { name: 'Approve invoice INV-1001' }).click();

  await page.getByRole('button', { name: 'Sign out' }).click();
});

