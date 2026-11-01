import { test, expect } from '@playwright/test';

test('frontend create invoice and submit for approval', async ({ page }) => {
  await page.goto('http://localhost:8080/');

  await page.getByRole('textbox', { name: 'Username' }).fill('demo.director');
  await page.getByRole('textbox', { name: 'Password' }).fill('ChangeMe123!');
  await page.getByRole('button', { name: 'Sign in' }).click();

  await page.getByRole('textbox', { name: 'Customer name' }).fill('test');
  await page.getByRole('spinbutton', { name: 'Amount' }).fill('100');
  await page.getByRole('button', { name: 'Create invoice' }).click();

  await page.getByRole('button', { name: /Submit invoice .* for approval/i }).click();
});

