// Tag Component - Design System v1.0
import React from 'react';
import './Tag.css';

interface TagProps {
  variant?: 'default' | 'success' | 'warning' | 'danger' | 'info';
  size?: 'sm' | 'md';
  children: React.ReactNode;
  className?: string;
}

export const Tag: React.FC<TagProps> = ({
  variant = 'default',
  size = 'sm',
  children,
  className = '',
}) => {
  return (
    <span className={`tag tag--${variant} tag--${size} ${className}`}>
      {children}
    </span>
  );
};
