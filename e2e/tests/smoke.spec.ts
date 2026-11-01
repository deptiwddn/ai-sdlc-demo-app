import { expect, test } from '@playwright/test';

test('smoke test: app loads and approver can sign in', async ({ page }) => {
  await page.goto('/');

  await expect(page.getByRole('heading', { name: 'Invoice Approvals' })).toBeVisible();

  await page.getByLabel('Username').fill('demo.approver');
  await page.getByLabel('Password').fill('ChangeMe123!');
  await page.getByRole('button', { name: 'Sign in' }).click();

  await expect(page.getByRole('button', { name: 'Sign out' })).toBeVisible();
  await expect(page.getByText('demo.approver (Approver)', { exact: true })).toBeVisible();
  await expect(page.getByRole('heading', { name: 'Invoices' })).toBeVisible();
});
