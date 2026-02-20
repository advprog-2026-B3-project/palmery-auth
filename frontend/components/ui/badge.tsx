import * as React from "react";
import { cn } from "@/lib/utils";

type BadgeVariant = "default" | "success" | "danger";

const variantClasses: Record<BadgeVariant, string> = {
  default: "ui-badge-default",
  success: "ui-badge-success",
  danger: "ui-badge-danger",
};

export interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: BadgeVariant;
}

export function Badge({ className, variant = "default", ...props }: BadgeProps) {
  return <span className={cn("ui-badge", variantClasses[variant], className)} {...props} />;
}
