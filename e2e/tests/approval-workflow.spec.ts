import { test, expect } from '@playwright/test';

test('approval workflow', async ({ page }) => {
  await page.goto('http://localhost:8080/');

  await page.getByRole('textbox', { name: 'Username' }).fill('demo.director');
  await page.getByRole('textbox', { name: 'Password' }).fill('ChangeMe123!');
  await page.getByRole('button', { name: 'Sign in' }).click();

  await page.getByRole('button', { name: /Submit invoice INV-1005 for/i }).click();
  await page.getByRole('tab', { name: 'Approvals' }).click();
  await page.getByRole('button', { name: /Approve invoice INV-/i }).first().click();
  await page.getByRole('button', { name: /Approve invoice INV-/i }).first().click();
  await page.getByRole('button', { name: /Approve invoice INV-/i }).first().click();
  await page.getByRole('button', { name: /Approve invoice INV-/i }).first().click();

  await page.getByRole('button', { name: 'Sign out' }).click();

  await page.getByRole('textbox', { name: 'Username' }).fill('demo.director2');
  await page.getByRole('textbox', { name: 'Password' }).fill('ChangeMe123!');
  await page.getByRole('button', { name: 'Sign in' }).click();

  await page.getByRole('button', { name: /Approve invoice INV-/i }).first().click();
});

