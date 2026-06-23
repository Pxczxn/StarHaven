// Toolbar Component - Design System v1.0
import React from 'react';
import './Toolbar.css';

interface ToolbarProps {
  left?: React.ReactNode;
  right?: React.ReactNode;
  className?: string;
}

export const Toolbar: React.FC<ToolbarProps> = ({
  left,
  right,
  className = '',
}) => {
  return (
    <div className={`toolbar ${className}`}>
      {left && <div className="toolbar__left">{left}</div>}
      {right && <div className="toolbar__right">{right}</div>}
    </div>
  );
};
