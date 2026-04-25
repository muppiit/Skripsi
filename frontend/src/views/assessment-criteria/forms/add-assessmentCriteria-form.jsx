/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useEffect } from "react";
import { Form, Input, Modal } from "antd";

const { TextArea } = Input;

const AddAssessmentCriteriaForm = ({ visible, onCancel, onOk, confirmLoading }) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (visible) {
      form.resetFields();
    }
  }, [visible, form]);

  const formItemLayout = {
    labelCol: { xs: { span: 24 }, sm: { span: 8 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  };

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      onOk(values);
    } catch (info) {
      console.log("Validate Failed:", info);
    }
  };

  return (
    <Modal
      title="Tambah Penilaian"
      open={visible}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      onOk={handleOk}
      confirmLoading={confirmLoading}
      okText="Simpan"
      cancelText="Batal"
    >
      <Form {...formItemLayout} form={form}>
        <Form.Item
          label="Nama Penilaian:"
          name="name"
          rules={[{ required: true, message: "Silahkan isikan nama penilaian" }]}
        >
          <Input placeholder="Nama Penilaian" />
        </Form.Item>

        <Form.Item
          label="Deskripsi Penilaian:"
          name="description"
          rules={[{ required: true, message: "Silahkan isikan deskripsi penilaian" }]}
        >
          <TextArea rows={4} placeholder="Deskripsi Penilaian" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddAssessmentCriteriaForm;
