export type ValidationResult<T> =
  | { isValid: true; value: T }
  | { isValid: false; error: string };

export function validateLicenseGenerationInput(input: {
  durationAmount: string;
  maxDevices: string;
  customEnabled: boolean;
  customPrefix: string;
}): ValidationResult<{ durationAmount: number; maxDevices: number; customPrefix?: string }> {
  const durationAmount = Number(input.durationAmount);
  const maxDevices = Number(input.maxDevices);

  if (!Number.isInteger(durationAmount) || durationAmount < 1 || durationAmount > 5256000) {
    return { isValid: false, error: "Enter a whole activation duration between 1 and 5,256,000." };
  }
  if (!Number.isInteger(maxDevices) || maxDevices < 1 || maxDevices > 100) {
    return { isValid: false, error: "Enter a whole device limit between 1 and 100." };
  }
  if (input.customEnabled && !input.customPrefix.trim()) {
    return { isValid: false, error: "Enter a custom key name or disable custom key name." };
  }

  return {
    isValid: true,
    value: {
      durationAmount,
      maxDevices,
      customPrefix: input.customEnabled ? input.customPrefix.trim() : undefined,
    },
  };
}

export function validateLicenseManagementInput(input: {
  maxDevices: string;
  requiresExpiry: boolean;
  expiry: string;
}): ValidationResult<{ maxDevices: number; expiresAt?: Date }> {
  const maxDevices = Number(input.maxDevices);
  if (!Number.isInteger(maxDevices) || maxDevices < 1 || maxDevices > 100) {
    return { isValid: false, error: "Enter a whole device limit between 1 and 100." };
  }

  if (input.requiresExpiry) {
    const expiresAt = new Date(input.expiry);
    if (Number.isNaN(expiresAt.getTime())) {
      return { isValid: false, error: "Enter a valid expiry date and time." };
    }
    return { isValid: true, value: { maxDevices, expiresAt } };
  }

  return { isValid: true, value: { maxDevices } };
}
