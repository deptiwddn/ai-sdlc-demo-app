import { expect, test } from '@playwright/test';
import { InvoiceAppPage } from '../pages/invoiceAppPage';

test.describe('approval enhancement', () => {
  test('manager approval limit validation', async ({ page }) => {
    const app = new InvoiceAppPage(page);
    await app.open();
    await app.login('demo.manager', 'ChangeMe123!');

    const invoiceId = await app.createInvoice('Manager Limit Case', 50001);
    await page.getByRole('button', { name: `Submit invoice ${invoiceId} for approval` }).click();
    await app.openApprovalsTab();
    await app.approveInvoice(invoiceId);

    await app.expectDecisionError('Manager approval limit exceeded');
    const row = await app.getInvoiceRow(invoiceId);
    await expect(row).toContainText('Pending Approval');
  });

  test('director approval limit validation', async ({ page }) => {
    const app = new InvoiceAppPage(page);
    await app.open();
    await app.login('demo.director', 'ChangeMe123!');

    const invoiceId = await app.createInvoice('Director Limit Case', 500001);
    await page.getByRole('button', { name: `Submit invoice ${invoiceId} for approval` }).click();
    await app.openApprovalsTab();
    await app.approveInvoice(invoiceId);

    const row = await app.getInvoiceRow(invoiceId);
    await expect(row).toContainText('Pending Second Approval');
    await expect(page.locator('#decision-error')).toBeHidden();
  });

  test('successful approval below approval threshold', async ({ page }) => {
    const app = new InvoiceAppPage(page);
    await app.open();
    await app.login('demo.approver', 'ChangeMe123!');

    const invoiceId = await app.createInvoice('Below Threshold', 1500);
    await page.getByRole('button', { name: `Submit invoice ${invoiceId} for approval` }).click();
    await app.openApprovalsTab();
    await app.approveInvoice(invoiceId);

    const row = await app.getInvoiceRow(invoiceId);
    await expect(row).toContainText('Approved');
  });

  test('approval rejection above role limit', async ({ page }) => {
    const app = new InvoiceAppPage(page);
    await app.open();
    await app.login('demo.manager', 'ChangeMe123!');

    const invoiceId = await app.createInvoice('Manager Over Limit', 50001);
    await page.getByRole('button', { name: `Submit invoice ${invoiceId} for approval` }).click();
    await app.openApprovalsTab();
    await app.approveInvoice(invoiceId);

    await app.expectDecisionError('Manager approval limit exceeded');
  });

  test('two-approver workflow for invoices above 500000', async ({ page }) => {
    const app = new InvoiceAppPage(page);
    await app.open();
    await app.login('demo.director', 'ChangeMe123!');

    const invoiceId = await app.createInvoice('Two Approver Workflow', 600000);
    await page.getByRole('button', { name: `Submit invoice ${invoiceId} for approval` }).click();
    await app.openApprovalsTab();
    await app.approveInvoice(invoiceId);

    await expect(page.locator('#decision-error')).toBeHidden();
    const row = await app.getInvoiceRow(invoiceId);
    await expect(row).toContainText('Pending Second Approval');

    await page.getByRole('button', { name: 'Sign out' }).click();
    await app.login('demo.director2', 'ChangeMe123!');
    await app.openApprovalsTab();
    await app.approveInvoice(invoiceId);

    const finalRow = await app.getInvoiceRow(invoiceId);
    await expect(finalRow).toContainText('Approved');
  });

  test('duplicate approver validation', async ({ page }) => {
    const app = new InvoiceAppPage(page);
    await app.open();
    await app.login('demo.director', 'ChangeMe123!');

    const invoiceId = await app.createInvoice('Duplicate Approver', 600000);
    await page.getByRole('button', { name: `Submit invoice ${invoiceId} for approval` }).click();
    await app.openApprovalsTab();
    await app.approveInvoice(invoiceId);

    await page.getByRole('button', { name: 'Sign out' }).click();
    await app.login('demo.director', 'ChangeMe123!');
    await app.openApprovalsTab();
    await app.approveInvoice(invoiceId);

    await app.expectDecisionError('same user cannot approve the same high-value invoice twice');
  });

  test('validation error message display', async ({ page }) => {
    const app = new InvoiceAppPage(page);
    await app.open();
    await app.login('demo.manager', 'ChangeMe123!');

    const invoiceId = await app.createInvoice('Validation Message Case', 50001);
    await page.getByRole('button', { name: `Submit invoice ${invoiceId} for approval` }).click();
    await app.openApprovalsTab();
    await app.approveInvoice(invoiceId);

    await app.expectDecisionError('Manager approval limit exceeded');
    await expect(page.locator('#decision-error')).toBeVisible();
  });

  test('invoice state remains unchanged after failed approval', async ({ page }) => {
    const app = new InvoiceAppPage(page);
    await app.open();
    await app.login('demo.manager', 'ChangeMe123!');

    const invoiceId = await app.createInvoice('No Mutation Case', 50001);
    await page.getByRole('button', { name: `Submit invoice ${invoiceId} for approval` }).click();
    await app.openApprovalsTab();
    await app.approveInvoice(invoiceId);

    await app.expectDecisionError('Manager approval limit exceeded');
    const currentState = await app.getInvoiceState(invoiceId);
    expect(currentState).toBe('PendingApproval');
  });
});
